package com.lokoko.domain.image.domain.entity;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.image.domain.entity.enums.ImageType;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.*;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CampaignImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_image_id")
    private Long id;

    private String url;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    private int displayOrder;

    private ImageType imageType;

    public static CampaignImage createCampaignImage(String imageUrl, int displayOrder, ImageType imageType, Campaign campaign) {
        return CampaignImage.builder()
                .url(imageUrl)
                .campaign(campaign)
                .displayOrder(displayOrder)
                .imageType(imageType)
                .build();
    }
}
