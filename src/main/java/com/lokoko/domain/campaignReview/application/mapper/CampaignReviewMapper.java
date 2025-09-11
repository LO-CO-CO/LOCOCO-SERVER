package com.lokoko.domain.campaignReview.application.mapper;

import com.lokoko.domain.campaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.campaignReview.api.dto.request.FirstReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.request.SecondReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.response.ReviewUploadResponse;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import org.springframework.stereotype.Component;

@Component
public class CampaignReviewMapper {

    public CampaignReview toFirstReview(CreatorCampaign creatorCampaign,
                                        FirstReviewUploadRequest request) {
        CampaignReview campaignReview = new CampaignReview();
        campaignReview.bindToCreatorCampaign(creatorCampaign);
        campaignReview.designateRound(ReviewRound.FIRST);
        campaignReview.chooseContentType(request.contentType());
        campaignReview.requestFirstReview(request.captionWithHashtags());
        return campaignReview;
    }

    public CampaignReview toSecondReview(CreatorCampaign creatorCampaign,
                                         SecondReviewUploadRequest secondReviewUploadRequest) {
        CampaignReview campaignReview = new CampaignReview();
        campaignReview.bindToCreatorCampaign(creatorCampaign);
        campaignReview.designateRound(ReviewRound.SECOND);
        campaignReview.chooseContentType(secondReviewUploadRequest.contentType());
        campaignReview.requestSecondReview(secondReviewUploadRequest.captionWithHashtags(),
                secondReviewUploadRequest.postUrl());
        return campaignReview;
    }

    public ReviewUploadResponse toUploadResponse(CampaignReview saved) {
        return ReviewUploadResponse.builder()
                .reviewId(saved.getId())
                .build();
    }
}
