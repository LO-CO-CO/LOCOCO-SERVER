package com.lokoko.domain.campaignReview.domain.repository;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReviewVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignReviewVideoRepository extends JpaRepository<CampaignReviewVideo, Long>,
        CampaignReviewVideoRepositoryCustom {
    void deleteAllByCampaignReview(CampaignReview campaignReview);
}
