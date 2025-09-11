package com.lokoko.domain.image.domain.repository;

import com.lokoko.domain.image.domain.entity.CampaignReviewImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignReviewImageRepository extends JpaRepository<CampaignReviewImage, Long> {

    List<CampaignReviewImage> findAllByCampaignReviewIdOrderByIdAsc(Long campaignReviewId);

    long countByCampaignReviewId(Long campaignReviewId);
}
