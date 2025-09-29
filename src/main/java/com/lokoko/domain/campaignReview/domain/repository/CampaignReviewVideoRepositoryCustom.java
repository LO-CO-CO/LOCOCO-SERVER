package com.lokoko.domain.campaignReview.domain.repository;

import java.util.List;

public interface CampaignReviewVideoRepositoryCustom {

    List<String> findVideoUrlsByReviewIdOrderByDisplay(Long campaignReviewId);
}
