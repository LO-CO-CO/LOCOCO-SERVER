package com.lokoko.domain.creator.application.service;

import com.lokoko.global.auth.provider.tiktok.TikTokOAuthClient;
import com.lokoko.global.auth.provider.tiktok.dto.TikTokProfileDto;
import com.lokoko.global.auth.provider.tiktok.dto.TikTokVideoListResponse;
import com.lokoko.global.auth.provider.tiktok.service.TikTokRedisTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * TikTok API 호출 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokApiService {

    private final TikTokRedisTokenService tikTokRedisTokenService;
    private final TikTokOAuthClient tikTokOAuthClient;
    private final CreatorGetService creatorGetService;

    /**
     * Creator의 TikTok 프로필 정보 조회
     */
    public TikTokProfileDto getCreatorProfile(Long creatorId) {
        String accessToken = tikTokRedisTokenService.getValidAccessToken(creatorId);
        return tikTokOAuthClient.fetchProfile(accessToken);
    }

    /**
     * Creator의 TikTok 특정 영상들 조회 (video ID 기반)
     */
    public TikTokVideoListResponse getCreatorVideosByIds(Long creatorId, Long videoId) {
        String accessToken = tikTokRedisTokenService.getValidAccessToken(creatorId);
        return tikTokOAuthClient.queryVideosByIds(accessToken, videoId);
    }
}