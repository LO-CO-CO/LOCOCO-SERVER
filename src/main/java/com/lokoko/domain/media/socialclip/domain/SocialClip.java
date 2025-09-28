package com.lokoko.domain.media.socialclip.domain;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.*;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialClip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_clip_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "campaign_review_id")
    private CampaignReview campaignReview;

    Long plays;

    Long likes;

    Long comments;

    Long shares;

    private Instant uploadedAt;
}
