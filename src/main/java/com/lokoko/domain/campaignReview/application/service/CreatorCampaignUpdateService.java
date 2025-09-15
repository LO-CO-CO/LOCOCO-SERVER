package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatorCampaignUpdateService {

    private final CreatorCampaignRepository creatorCampaignRepository;
    private final CampaignReviewGetService campaignReviewGetService;

    @Transactional
    public void refreshParticipationStatus(Long creatorCampaignId) {
        CreatorCampaign creatorCampaign = creatorCampaignRepository.getByIdForUpdate(creatorCampaignId);

        boolean firstExists = campaignReviewGetService.existsFirst(creatorCampaignId);
        boolean secondExists = campaignReviewGetService.existsSecond(creatorCampaignId);

        ParticipationStatus nextStatus;
        if (!Boolean.TRUE.equals(creatorCampaign.getAddressConfirmed())) {
            nextStatus = ParticipationStatus.APPROVED;
        } else if (secondExists) {
            nextStatus = ParticipationStatus.APPROVED_SECOND_REVIEW_DONE;
        } else if (firstExists) {
            nextStatus = ParticipationStatus.APPROVED_FIRST_REVIEW_DONE;
        } else {
            nextStatus = ParticipationStatus.APPROVED_ADDRESS_CONFIRMED;
        }

        if (creatorCampaign.getStatus() != nextStatus) {
            creatorCampaign.changeStatus(nextStatus);
        }
    }

}
