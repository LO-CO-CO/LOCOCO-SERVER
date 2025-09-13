package com.lokoko.global.auth.tiktok.service;

import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import com.lokoko.domain.creator.exception.CreatorNotFoundException;
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
    private final CreatorRepository creatorRepository;
    private final TikTokRedisTokenService tikTokRedisTokenService;

    public String generateConnectionUrl(Long creatorId) {

        creatorRepository.findByIdOrThrow(creatorId);
        return tikTokOAuthClient.buildAuthorizationUrl(creatorId);
    }

    @Transactional
    public TikTokConnectionResponse connectTikTok(Long creatorId, String code) {
        try {
            Creator creator = creatorRepository.findByIdOrThrow(creatorId);

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
            tikTokRedisTokenService.saveTokens(
                creatorId,
                tokenDto.accessToken(),
                tokenDto.refreshToken(),
                tokenDto.expiresIn(),
                Long.parseLong(tokenDto.refreshExpiresIn())
            );

            creator.connectTikTok(profileDto.openId());
            creatorRepository.save(creator);

            log.info("크리에이터 틱톡 연결 성공: {}, 틱톡 유저 id: {}", creatorId, profileDto.openId());

            return TikTokConnectionResponse.connected(profileDto.openId(), creator.getTikTokConnectedAt());

        } catch (OauthException e) {
            throw e;
        } catch (Exception e) {
            log.error("틱톡 계정 연결에 실패하였습니다.", e);
            throw new OauthException(ErrorMessage.TIKTOK_CONNECTION_FAILED);
        }
    }

}