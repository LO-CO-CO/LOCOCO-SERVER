package com.lokoko.domain.campaign.domain.entity;


import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.api.dto.request.CampaignCreateRequest;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.global.common.entity.BaseEntity;
import com.lokoko.global.common.enums.Language;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Stream;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Campaign extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(nullable = false)
    private String title;

    /**
     * 캠페인 진행언어
     */
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    /**
     * 캠페인 종류
     * ex. GIVEAWAY / CONTENTS / EXCLUSIVE
     */
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private CampaignType campaignType;

    /**
     * 캠페인 상태
     * ex. 임시 저장 / 대기 중 / 오픈 예정 / 진행 중 / 종료
     */
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus campaignStatus;

    @Column(nullable = false)
    private Instant applyStartDate;

    @Column(nullable = false)
    private Instant applyDeadline;

    @Column(nullable = false)
    private Instant creatorAnnouncementDate;

    @Column(nullable = false)
    private Instant reviewSubmissionDeadline;

    @Column(nullable = false)
    private int recruitmentNumber;

    /**
     * 크리에이터 참여 보상 목록
     */
    @ElementCollection
    @CollectionTable(
            name = "campaign_participation_rewards",
            joinColumns = @JoinColumn(name = "campaign_id")
    )
    @BatchSize(size = 5)
    private Set<String> participationRewards;

    /**
     * 크리에이터 제출 콘텐츠 목록
     */
    @ElementCollection
    @CollectionTable(
            name = "campaign_deliverable_requirements",
            joinColumns = @JoinColumn(name = "campaign_id")
    )
    @BatchSize(size = 5)
    private Set<String> deliverableRequirements;

    /**
     * 크리에이터 참여 조건 목록
     */
    @ElementCollection
    @CollectionTable(
            name = "campaign_eligibility_requirements",
            joinColumns = @JoinColumn(name = "campaign_id")
    )
    @BatchSize(size = 5)
    private Set<String> eligibilityRequirements;

    /**
     * 캠페인 상태 변경
     * @param newStatus
     */
    public void changeStatus(CampaignStatus newStatus) {
        this.campaignStatus = newStatus;
    }

    /**
     * 임시 저장 여부 반환
     * 필수 필드 중 하나라도 비어있으면 임시저장으로 간주.
     * @return 임시저장 여부
     */
    public boolean isDraft() {
        return Stream.of(
                this.brand == null,
                this.title == null || this.title.isBlank(),
                this.campaignType == null,
                this.applyStartDate == null,
                this.applyDeadline == null,
                this.creatorAnnouncementDate == null,
                this.reviewSubmissionDeadline == null,
                this.recruitmentNumber <= 0,
                this.participationRewards == null || this.participationRewards.isEmpty(),
                this.deliverableRequirements == null || this.deliverableRequirements.isEmpty(),
                this.eligibilityRequirements == null || this.eligibilityRequirements.isEmpty()
        ).anyMatch(condition -> condition);
    }

    public static Campaign createCampaign(CampaignCreateRequest request, Brand brand){
        return Campaign.builder()
                .brand(brand)
                .title(request.campaignTitle())
                .language(request.language())
                .campaignType(request.campaignType())
                .campaignStatus(CampaignStatus.DRAFT)  // 초기 상태는 DRAFT
                .applyStartDate(request.applyStartDate())
                .applyDeadline(request.applyDeadline())
                .creatorAnnouncementDate(request.creatorAnnouncementDate())
                .reviewSubmissionDeadline(request.reviewSubmissionDeadline())
                .recruitmentNumber(request.recruitmentNumber())
                .participationRewards(request.participationRewards())
                .deliverableRequirements(request.deliverableRequirements())
                .eligibilityRequirements(request.eligibilityRequirements())
                .build();
    }























}
