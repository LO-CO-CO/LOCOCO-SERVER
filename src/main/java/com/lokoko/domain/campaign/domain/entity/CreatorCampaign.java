package com.lokoko.domain.campaign.domain.entity;

import com.lokoko.domain.campaign.domain.entity.enums.ParticipationStatus;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

import static jakarta.persistence.FetchType.*;

@Getter
@Entity
@Table(name = "creator_campaign")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class CreatorCampaign extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "creator_id")
    private Creator creator;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    /**
     * 크리에이터가 캠페인을 지원했을 때의 세부 상태 현황
     * APPROVED(승인됨) , REJECTED(거절됨) , PENDING(대기중) 등....
     */
    @Enumerated(value = EnumType.STRING)
    private ParticipationStatus status;

    // 캠페인에 지원한 날짜
    @Column(nullable = false)
    private Instant appliedAt;

    // 배송지 입력 여부 및 시간
    private Boolean addressConfirmed;
    private Instant addressConfirmedAt;

    // 1차 리뷰 업로드 여부 및 시간
    private Boolean firstReviewSubmitted;
    private Instant firstReviewSubmittedAt;

    // 수정 요청 여부 및 시간
    private Boolean revisionRequested;
    private Instant revisionRequestedAt;

    // 2차 리뷰 업로드 여부 및 시간
    private Boolean secondReviewSubmitted;
    private Instant secondReviewSubmittedAt;

    public void changeAddressConfirmed(boolean addressConfirmed) {
        this.addressConfirmed = addressConfirmed;
        this.addressConfirmedAt = Instant.now();
    }

    public void changeFirstReviewSubmitted(boolean firstReviewSubmitted) {
        this.firstReviewSubmitted = firstReviewSubmitted;
        this.firstReviewSubmittedAt = Instant.now();
    }

    public void changeRevisionRequested(boolean revisionRequested) {
        this.revisionRequested = revisionRequested;
        this.revisionRequestedAt = Instant.now();
    }

    public void changeSecondReviewSubmitted(boolean secondReviewSubmitted) {
        this.secondReviewSubmitted = secondReviewSubmitted;
        this.secondReviewSubmittedAt = Instant.now();
    }

    public void changeStatus(ParticipationStatus newStatus) {
        this.status = newStatus;
    }



}
