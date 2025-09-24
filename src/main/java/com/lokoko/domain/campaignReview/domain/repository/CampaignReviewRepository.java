package com.lokoko.domain.campaignReview.domain.repository;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignReviewRepository extends JpaRepository<CampaignReview, Long> {

    boolean existsByCreatorCampaignIdAndReviewRound(Long creatorCampaignId, ReviewRound reviewRound);

    @Query("select r.contentType from CampaignReview r " +
            "where r.creatorCampaign.id = :creatorCampaignId and r.reviewRound = :reviewRound")
    Optional<ContentType> findContentOnly(@Param("creatorCampaignId") Long creatorCampaignId,
                                          @Param("reviewRound") ReviewRound reviewRound);

    Optional<CampaignReview> findByCreatorCampaignIdAndReviewRound(Long creatorCampaignId, ReviewRound reviewRound);

    List<CampaignReview> findAllByCreatorCampaignIdOrderByIdAsc(Long creatorCampaignId);

    Optional<CampaignReview> findByCreatorCampaignAndReviewRound(CreatorCampaign creatorCampaign,
                                                                 ReviewRound reviewRound);
}
