package com.lokoko.global.auth.tiktok.service;

import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import com.lokoko.domain.creator.exception.CreatorNotFoundException;
import com.lokoko.global.auth.tiktok.TikTokOAuthClient;
import com.lokoko.global.auth.tiktok.dto.TikTokProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TikTok API 호출 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokApiService {

    private final TikTokRedisTokenService tikTokRedisTokenService;
    private final TikTokOAuthClient tikTokOAuthClient;
    private final CreatorRepository creatorRepository;

    /**
     * Creator의 TikTok 프로필 정보 조회
     */
    public TikTokProfileDto getCreatorProfile(Long creatorId) {
        String accessToken = tikTokRedisTokenService.getValidAccessToken(creatorId);
        return tikTokOAuthClient.fetchProfile(accessToken);
    }

    /**
     * Creator의 TikTok 연결 상태 확인
     */
    public boolean isConnected(Long creatorId) {
        Creator creator = creatorRepository.findByIdOrThrow(creatorId);

        return creator.getTikTokUserId() != null &&
               tikTokRedisTokenService.hasTokens(creatorId);
    }

    /**
     * Creator의 TikTok 연결 해제
     */
    @Transactional
    public void disconnectTikTok(Long creatorId) {
        Creator creator = creatorRepository.findByIdOrThrow(creatorId);

        creator.disconnectTikTok();
        creatorRepository.save(creator);

        // Redis에서 토큰 삭제
        tikTokRedisTokenService.deleteTokens(creatorId);
    }
}