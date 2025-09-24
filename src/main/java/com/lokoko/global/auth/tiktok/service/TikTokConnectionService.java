package com.lokoko.global.auth.tiktok.service;

import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import com.lokoko.domain.customer.domain.entity.Customer;
import com.lokoko.domain.customer.domain.repository.CustomerRepository;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.global.auth.exception.ErrorMessage;
import com.lokoko.global.auth.exception.OauthException;
import com.lokoko.global.auth.tiktok.TikTokOAuthClient;
import com.lokoko.global.auth.tiktok.dto.TikTokConnectionResponse;
import com.lokoko.global.auth.tiktok.dto.TikTokProfileDto;
import com.lokoko.global.auth.tiktok.dto.TikTokTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokConnectionService {
    
    private final TikTokOAuthClient tikTokOAuthClient;
    private final UserRepository userRepository;
    private final CreatorRepository creatorRepository;
    private final CustomerRepository customerRepository;
    private final TikTokRedisTokenService tikTokRedisTokenService;

    public String generateConnectionUrl(Long userId) {

        userRepository.findById(userId);
        return tikTokOAuthClient.buildAuthorizationUrl(userId);
    }

    @Transactional
    public TikTokConnectionResponse connectTikTok(Long userId, String code) {
        try {
            User user  = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);

            // 토큰 발급
            TikTokTokenDto tokenDto = tikTokOAuthClient.issueToken(code);
            if (tokenDto == null || tokenDto.accessToken() == null) {
                throw new OauthException(ErrorMessage.TIKTOK_TOKEN_REQUEST_FAILED);
            }

            // 사용자 정보 조회
            TikTokProfileDto profileDto = tikTokOAuthClient.fetchProfile(tokenDto.accessToken());
            if (profileDto == null) {
                throw new OauthException(ErrorMessage.TIKTOK_PROFILE_FETCH_FAILED);
            }

            // 토큰을 Redis에 저장
            saveTokensInRedis(userId, tokenDto);

            if (user.getRole() == Role.CREATOR){
                Creator creator = user.getCreator();
                creator.connectTikTok(profileDto.openId());
                creatorRepository.save(creator);

            } else if (user.getRole() == Role.CUSTOMER) {
                Customer customer = user.getCustomer();
                customer.connectTikTok(profileDto.openId());
                customerRepository.save(customer);
            }

            return TikTokConnectionResponse.connected(profileDto.openId());

        } catch (OauthException e) {
            throw e;
        } catch (Exception e) {
            log.error("틱톡 계정 연결에 실패하였습니다.", e);
            throw new OauthException(ErrorMessage.TIKTOK_CONNECTION_FAILED);
        }
    }

    private void saveTokensInRedis(Long userId, TikTokTokenDto tokenDto) {
        tikTokRedisTokenService.saveTokens(userId,
            tokenDto.accessToken(),
            tokenDto.refreshToken(),
            tokenDto.expiresIn(),
            tokenDto.refreshExpiresIn()
        );
    }

}