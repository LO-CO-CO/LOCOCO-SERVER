package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.brand.api.dto.request.BrandNoteRevisionRequest;
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
    public String requestReviewRevision(RevisionAction action, Long campaignReviewId, BrandNoteRevisionRequest revisionRequest) {
        CampaignReview campaignReview = campaignReviewGetService.findById(campaignReviewId);
        String brandNote = revisionRequest.brandNote();

        if (action == RevisionAction.SAVE_DRAFT) {
           campaignReview.saveRequestRevision(brandNote);
        } else if (action == RevisionAction.SUBMIT){
            campaignReview.submitRequestRevision(brandNote);
        }
        return brandNote;
    }
}
