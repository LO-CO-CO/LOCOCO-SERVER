package com.lokoko.domain.creatorCampaign.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
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
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
     * 크리에이터가 캠페인을 지원했을 때의 세부 상태 현황 APPROVED(승인됨) , REJECTED(거절됨) , PENDING(대기중) 등....
     */
    @Enumerated(value = EnumType.STRING)
    private ParticipationStatus status;

    // 캠페인에 지원한 날짜
    @Column(nullable = false)
    private Instant appliedAt;

    // 배송지 입력 여부 및 시간
    private Boolean addressConfirmed;
    private Instant addressConfirmedAt;

    public void changeAddressConfirmed(boolean addressConfirmed) {
        this.addressConfirmed = addressConfirmed;
        this.addressConfirmedAt = Instant.now();
    }

    public void changeStatus(ParticipationStatus newStatus) {
        this.status = newStatus;
    }
}
