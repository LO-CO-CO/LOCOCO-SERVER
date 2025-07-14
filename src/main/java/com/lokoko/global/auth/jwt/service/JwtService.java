package com.lokoko.global.auth.jwt.service;

import static com.lokoko.global.auth.jwt.utils.JwtProvider.REFRESH_TOKEN_HEADER;

import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.global.auth.jwt.dto.GenerateTokenDto;
import com.lokoko.global.auth.jwt.dto.JwtTokenResponse;
import com.lokoko.global.auth.jwt.exception.RefreshTokenNotFoundException;
import com.lokoko.global.auth.jwt.exception.TokenExpiredException;
import com.lokoko.global.auth.jwt.exception.TokenInvalidException;
import com.lokoko.global.auth.jwt.utils.JwtExtractor;
import com.lokoko.global.auth.jwt.utils.JwtProvider;
import com.lokoko.global.utils.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";
    private static final String KEY_DELIMITER = ":";
    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    @Value("${lokoko.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    public JwtTokenResponse generateJwtToken(GenerateTokenDto dto) {
        String tokenId = UUID.randomUUID().toString();
        String accessToken = jwtProvider.generateAccessToken(dto.id(), dto.role(), dto.lineId());
        String refreshToken = jwtProvider.generateRefreshToken(dto.id(), dto.role(), tokenId, dto.lineId());

        String redisKey = "refreshToken:" + dto.id() + ":" + tokenId;
        redisUtil.setRefreshToken(redisKey, refreshToken, refreshTokenExpiration);

        return JwtTokenResponse.of(accessToken, refreshToken, tokenId);
    }

    public String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new RefreshTokenNotFoundException();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> REFRESH_TOKEN_HEADER.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(TokenInvalidException::new);
    }

    public JwtTokenResponse reissueJwtToken(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookies(request);

        Long userId = jwtExtractor.getId(refreshToken);
        String tokenId = jwtExtractor.getTokenId(refreshToken);
        String redisKey = REFRESH_TOKEN_KEY_PREFIX + userId + KEY_DELIMITER + tokenId;
        String stored = redisUtil.getRefreshToken(redisKey);

        if (!MessageDigest.isEqual(
                stored.getBytes(StandardCharsets.UTF_8),
                refreshToken.getBytes(StandardCharsets.UTF_8)
        )) {
            throw new TokenInvalidException();
        }

        if (jwtExtractor.isExpired(refreshToken)) {
            throw new TokenExpiredException();
        }

        String role = jwtExtractor.getRole(refreshToken);
        String lineId = jwtExtractor.getLineId(refreshToken);
        JwtTokenResponse newTokens = generateJwtToken(GenerateTokenDto.of(userId, role, lineId));

        redisUtil.deleteRefreshToken(redisKey);
        return newTokens;
    }

    public JwtTokenResponse issueTokensForTest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String tokenId = UUID.randomUUID().toString();
        String fakeLineId = user.getId().toString();

        String accessToken = jwtProvider.generateAccessToken(
                user.getId(),
                user.getRole().name(),
                fakeLineId
        );
        String refreshToken = jwtProvider.generateRefreshToken(
                user.getId(),
                user.getRole().name(),
                tokenId,
                fakeLineId
        );

        return JwtTokenResponse.of(accessToken, refreshToken, tokenId);
    }
}
