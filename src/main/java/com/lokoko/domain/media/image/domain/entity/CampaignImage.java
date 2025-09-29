package com.lokoko.domain.media.image.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.media.domain.MediaFile;
import com.lokoko.domain.media.image.domain.entity.enums.ImageType;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CampaignImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_image_id")
    private Long id;

    @Embedded
    private MediaFile mediaFile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    private int displayOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    public static CampaignImage createCampaignImage(MediaFile mediaFile, int displayOrder, ImageType imageType,
                                                    Campaign campaign) {
        return CampaignImage.builder()
                .mediaFile(mediaFile)
                .campaign(campaign)
                .displayOrder(displayOrder)
                .imageType(imageType)
                .build();
    }
}
