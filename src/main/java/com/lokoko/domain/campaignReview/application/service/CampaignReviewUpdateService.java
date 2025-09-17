package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.brand.api.dto.request.BrandNoteRevisionRequest;
import com.lokoko.domain.brand.api.dto.response.BrandNoteRevisionResponse;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.RevisionAction;
import com.lokoko.domain.campaignReview.exception.RevisionRequestNotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignReviewUpdateService {

    private final CampaignReviewGetService campaignReviewGetService;

    @Transactional
    public BrandNoteRevisionResponse requestReviewRevision(RevisionAction action, Long brandId, Long campaignReviewId, BrandNoteRevisionRequest revisionRequest) {

        // 캠페인 리뷰가 브랜드와 관련 된 것이 아니면, 수정사항을 남길 수 없다.
        CampaignReview campaignReview = campaignReviewGetService.findById(campaignReviewId);
        validateBrandOwnsCampaign(brandId, campaignReview);

        // 1차 제출 리뷰가 아니라면 수정 요청 불가
        validateReviewStatus(campaignReview);
        String brandNote = revisionRequest.brandNote();

        // action 에 따라, 임시저장할지 전달할지 결정
        if (action == RevisionAction.SAVE_DRAFT) {
           campaignReview.saveRequestRevision(brandNote);
        } else if (action == RevisionAction.SUBMIT){
            campaignReview.submitRequestRevision(brandNote);
        }

        // 응답 DTO 생성하여 반환
        return new BrandNoteRevisionResponse(
                campaignReview.getBrandNote(),
                campaignReview.getBrandNoteStatus(),
                campaignReview.getRevisionRequestedAt()
        );
    }

    private static void validateBrandOwnsCampaign(Long brandId, CampaignReview campaignReview) {
        if (!campaignReview.getCreatorCampaign().getCampaign().getBrand().getId().equals(brandId)){
            throw new NotCampaignOwnershipException();
        }
    }

    private static void validateReviewStatus(CampaignReview review){
        if (review.getStatus() != ReviewStatus.SUBMITTED){
            throw new RevisionRequestNotAllowedException();
        }
    }
}
