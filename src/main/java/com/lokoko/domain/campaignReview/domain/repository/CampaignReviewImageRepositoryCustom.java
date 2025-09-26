package com.lokoko.domain.campaignReview.domain.repository;

import java.util.List;

public interface CampaignReviewImageRepositoryCustom {

    List<String> findImageUrlsByReviewIdOrderByDisplay(Long campaignReviewId);
}
