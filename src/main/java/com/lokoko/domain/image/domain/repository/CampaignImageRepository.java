package com.lokoko.domain.image.domain.repository;

import com.lokoko.domain.campaign.api.dto.response.CampaignImageResponse;
import com.lokoko.domain.image.domain.entity.CampaignImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CampaignImageRepository extends JpaRepository<CampaignImage, Long> {

    @Query("SELECT new com.lokoko.domain.campaign.api.dto.response.CampaignImageResponse(ci.id, ci.mediaFile.fileUrl, ci.displayOrder) " +
            "FROM CampaignImage ci " +
            "WHERE ci.campaign.id = :campaignId " +
            "AND ci.imageType = com.lokoko.domain.image.domain.entity.enums.ImageType.THUMBNAIL " +
            "ORDER BY ci.displayOrder")
    List<CampaignImageResponse> findThumbnailImagesByCampaignId(@Param("campaignId") Long campaignId);

    @Query("SELECT new com.lokoko.domain.campaign.api.dto.response.CampaignImageResponse(ci.id, ci.mediaFile.fileUrl, ci.displayOrder) " +
            "FROM CampaignImage ci " +
            "WHERE ci.campaign.id = :campaignId " +
            "AND ci.imageType = com.lokoko.domain.image.domain.entity.enums.ImageType.DETAIL " +
            "ORDER BY ci.displayOrder")
    List<CampaignImageResponse> findDetailImagesByCampaignId(@Param("campaignId") Long campaignId);

    @Modifying
    @Query("DELETE FROM CampaignImage ci WHERE ci.campaign.id = :campaignId")
    void deleteByCampaignId(@Param("campaignId") Long campaignId);


}
