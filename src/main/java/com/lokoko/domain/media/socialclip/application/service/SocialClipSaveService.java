package com.lokoko.domain.media.socialclip.application.service;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.media.socialclip.domain.SocialClip;
import com.lokoko.domain.media.socialclip.domain.repository.SocialClipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SocialClipSaveService {

    private final SocialClipRepository socialClipRepository;

    /**
     * 2차 리뷰 작성 시 SocialClip 생성
     * 조회수, 좋아요수, 댓글수, 공유수 모두 기본 0으로 세팅
     */
    @Transactional
    public void createForSecondReview(CampaignReview campaignReview) {
        SocialClip socialClip = SocialClip.builder()
                .campaignReview(campaignReview)
                .plays(0L)
                .likes(0L)
                .comments(0L)
                .shares(0L)
                .uploadedAt(Instant.now())
                .build();

        socialClipRepository.save(socialClip);
    }
}