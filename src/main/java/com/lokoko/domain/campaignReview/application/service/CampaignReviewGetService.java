package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewRepository;
import com.lokoko.domain.campaignReview.exception.ReviewAlreadySubmittedException;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignReviewGetService {

    private final CampaignReviewRepository campaignReviewRepository;

    public void assertRoundNotExists(Long creatorCampaignId, ReviewRound reviewRound) {
        if (campaignReviewRepository.existsByCreatorCampaignIdAndReviewRound(creatorCampaignId, reviewRound)) {
            throw new ReviewAlreadySubmittedException();
        }
    }

    public Optional<ContentType> findFirstContent(Long creatorCampaignId) {
        return campaignReviewRepository.findContentOnly(creatorCampaignId, ReviewRound.FIRST);
    }

    /**
     * 특정 CreatorCampaign 내 라운드 존재 여부
     */
    public boolean existsRound(Long creatorCampaignId, ReviewRound reviewRound) {
        return campaignReviewRepository.existsByCreatorCampaignIdAndReviewRound(creatorCampaignId, reviewRound);
    }

    public boolean existsFirst(Long creatorCampaignId) {
        return existsRound(creatorCampaignId, ReviewRound.FIRST);
    }

    public boolean existsSecond(Long creatorCampaignId) {
        return existsRound(creatorCampaignId, ReviewRound.SECOND);
    }
}
