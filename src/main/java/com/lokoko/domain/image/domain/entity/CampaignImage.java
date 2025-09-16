package com.lokoko.domain.image.domain.entity;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.image.domain.entity.enums.ImageType;
import com.lokoko.global.common.entity.BaseEntity;
import com.lokoko.global.common.entity.MediaFile;
import com.lokoko.global.utils.S3UrlParser;
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

    @Embedded
    private MediaFile mediaFile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    private int displayOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    public static CampaignImage createCampaignImage(String imageUrl, int displayOrder, ImageType imageType, Campaign campaign) {
        MediaFile mediaFile = S3UrlParser.parsePresignedUrl(imageUrl);
        return CampaignImage.builder()
                .mediaFile(mediaFile)
                .campaign(campaign)
                .displayOrder(displayOrder)
                .imageType(imageType)
                .build();
    }
}
