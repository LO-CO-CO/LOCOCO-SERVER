package com.lokoko.domain.campaignReview.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.lokoko.domain.campaignReview.domain.entity.enums.BrandNoteStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.common.entity.BaseEntity;
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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "campaign_reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_campaign_review_campaign_round",
                        columnNames = {"creator_campaign_id", "review_round"}
                )
        }
)
@NoArgsConstructor
public class CampaignReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_review_id")
    private Long id;

    @Column(length = 2200)
    private String captionWithHashtags;

    @Column(length = 1024)
    private String postUrl;

    @Column(length = 1000)
    private String brandNote;

    @Enumerated(EnumType.STRING)
    private BrandNoteStatus brandNoteStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "review_round")
    private ReviewRound reviewRound;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "creator_campaign_id", nullable = false)
    private CreatorCampaign creatorCampaign;

    private Instant revisionRequestedAt;

    /**
     * 리뷰 생성시, 캠페인에 할당 메서드
     */
    public void bindToCreatorCampaign(CreatorCampaign creatorCampaign) {
        this.creatorCampaign = creatorCampaign;
    }

    /**
     * 리뷰 라운드 지정 (1차, 2차)
     */
    public void designateRound(ReviewRound reviewRound) {
        this.reviewRound = reviewRound;
    }

    /**
     * SNS 콘텐츠 유형 선택시 호출 메서드 (인스타 게시물, 인스타 릴스, 틱톡 비디오)
     */
    public void chooseContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * 1차 리뷰 작성 후 호출 메서드
     */
    public void requestFirstReview(String captionWithHashtags) {
        this.captionWithHashtags = captionWithHashtags;
        this.status = ReviewStatus.SUBMITTED;
    }

    /**
     * 브랜드가 1차 리뷰 작성 후 수정 요청시 호출 메서드
     */
    public void submitRequestRevision(String brandNote) {
        this.brandNote = brandNote;
        this.brandNoteStatus = BrandNoteStatus.PUBLISHED;
        this.status = ReviewStatus.REVISION_REQUESTED;
        creatorCampaign.changeStatus(ParticipationStatus.APPROVED_REVISION_REQUESTED);
        this.revisionRequestedAt = Instant.now();
    }

    /**
     * 추후, 브랜드 노트 조회가 필요한 API 에서는 CampaignReview 의 BrandNoteStatus 가 DRAFT 인 경우, 아무것도 보여주지 않으면 됩니다.
     *
     * @param brandNote
     */
    public void saveRequestRevision(String brandNote) {
        this.brandNote = brandNote;
        this.brandNoteStatus = BrandNoteStatus.DRAFT;
    }

    /**
     * 브랜드의 수정 요청에 대해 크리에이터가 최종 제출시 호출 메서드
     */
    public void requestSecondReview(String captionWithHashtags, String postUrl) {
        this.captionWithHashtags = captionWithHashtags;
        this.postUrl = postUrl;
        this.status = ReviewStatus.RESUBMITTED;
    }
}
