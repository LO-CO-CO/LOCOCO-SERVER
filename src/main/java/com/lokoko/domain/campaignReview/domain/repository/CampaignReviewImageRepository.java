package com.lokoko.domain.campaignReview.domain.repository;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReviewImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignReviewImageRepository extends JpaRepository<CampaignReviewImage, Long>,
        CampaignReviewImageRepositoryCustom {
    List<CampaignReviewImage> findAllByCampaignReview_IdOrderByIdAsc(Long campaignReviewId);

    long countByCampaignReview_Id(Long campaignReviewId);

    void deleteAllByCampaignReview(CampaignReview review);
}
