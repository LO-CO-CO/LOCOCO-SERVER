package com.lokoko.domain.campaignReview.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.lokoko.domain.campaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "campaign_reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CampaignReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String caption;

    @Column(length = 2200)
    private String hashtags;

    @Column(length = 1024)
    private String postUrl;

    @Column(length = 1000)
    private String brandNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewRound reviewRound;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "creator_campaign_id", nullable = false)
    private CreatorCampaign creatorCampaign;

    /**
     * 1차 리뷰 작성 후 브랜드가 수정 요청시 호출할 메서드
     */
    public void requestRevision(String note) {
        this.brandNote = note;
        this.status = ReviewStatus.REVISION_REQUESTED;
    }

    /**
     * 브랜드의 수정 요청에 대해 크리에이터가 재제출할 때 호출할 메서드
     */
    public void resubmit(String caption, String hashtags, String postUrl) {
        this.caption = caption;
        this.hashtags = hashtags;
        this.postUrl = postUrl;
        this.status = ReviewStatus.RESUBMITTED;
    }
}
