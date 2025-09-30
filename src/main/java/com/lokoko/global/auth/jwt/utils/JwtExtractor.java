package com.lokoko.global.auth.jwt.utils;

import static com.lokoko.global.auth.jwt.utils.JwtProvider.AUTHORIZATION_HEADER;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtExtractor {
    private static final String BEARER = "Bearer ";
    private static final String ID_CLAIM = "id";
    private static final String ROLE_CLAIM = "role";
    private static final String LINE_CLAIM = "lineId";
    private final Key key;

    public JwtExtractor(@Value("${lokoko.jwt.key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Optional<String> extractJwtToken(HttpServletRequest request) {
        // 1. Authorization 헤더에서 토큰 추출 시도
        Optional<String> headerToken = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.replace(BEARER, ""));

        if (headerToken.isPresent()) {
            return headerToken;
        }

        // 2. 쿠키에서 Access Token 추출 시도
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> JwtProvider.ACCESS_TOKEN_HEADER.equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue);
        }

        return Optional.empty();
    }

    public Long getId(String token) {
        return parseIdClaim(token);
    }

    public String getLineId(String token) {
        return getClaimFromToken(token, LINE_CLAIM);
    }

    public String getRole(String token) {
        return getClaimFromToken(token, ROLE_CLAIM);
    }

    public String getTokenId(String token) {
        Claims claims = parseClaims(token);
        return claims.getId();
    }

    public Boolean isExpired(String token) {
        Claims claims = parseClaims(token);
        Date exp = claims.getExpiration();
        if (exp == null) {
            return false;
        }
        return claims.getExpiration().before(new Date());
    }

    private String getClaimFromToken(String token, String claimName) {
        Claims claims = parseClaims(token);
        return claims.get(claimName, String.class);
    }

    private Long parseIdClaim(String token) {
        return parseClaims(token).get(ID_CLAIM, Long.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateJwtToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build();
            parser.parseClaimsJws(token).getBody();
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
