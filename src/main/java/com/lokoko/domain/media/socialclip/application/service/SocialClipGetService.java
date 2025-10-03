package com.lokoko.domain.media.socialclip.application.service;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.media.socialclip.domain.SocialClip;
import com.lokoko.domain.media.socialclip.domain.repository.SocialClipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialClipGetService {

    private final SocialClipRepository socialClipRepository;

    /**
     * CampaignReview로 SocialClip 조회
     */
    public Optional<SocialClip> findByCampaignReview(CampaignReview campaignReview) {
        return socialClipRepository.findByCampaignReview(campaignReview);
    }
}
