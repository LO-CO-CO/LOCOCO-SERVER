package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.brand.api.dto.request.BrandNoteRevisionRequest;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.RevisionAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignReviewUpdateService {

    private final CampaignReviewGetService campaignReviewGetService;

    @Transactional
    public String requestReviewRevision(RevisionAction action, Long brandId, Long campaignReviewId, BrandNoteRevisionRequest revisionRequest) {

        // 캠페인 리뷰가 브랜드와 관련 된 것이 아니면, 수정사항을 남길 수 없다.
        CampaignReview campaignReview = campaignReviewGetService.findById(campaignReviewId);
        validateBrandOwnsCampaign(brandId, campaignReview);
        String brandNote = revisionRequest.brandNote();

        if (action == RevisionAction.SAVE_DRAFT) {
           campaignReview.saveRequestRevision(brandNote);
        } else if (action == RevisionAction.SUBMIT){
            campaignReview.submitRequestRevision(brandNote);
        }
        return brandNote;
    }

    private static void validateBrandOwnsCampaign(Long brandId, CampaignReview campaignReview) {
        if (!campaignReview.getCreatorCampaign().getCampaign().getBrand().getId().equals(brandId)){
            throw new NotCampaignOwnershipException();
        }
    }
}
