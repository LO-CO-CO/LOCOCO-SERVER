package com.lokoko.domain.campaign.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.api.dto.request.CampaignCreateRequest;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductType;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.domain.campaign.exception.DraftNotFilledException;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.domain.user.api.dto.request.CampaignModifyRequest;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@SQLRestriction("deleted = 0")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {@Index(name = "idx_campaign_status", columnList = "campaign_status"),
        @Index(name = "idx_published_at", columnList = "published_at")})
public class Campaign extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private String title;

    /**
     * 캠페인 진행언어
     */
    @Enumerated(value = EnumType.STRING)
    private CampaignLanguage language;

    /**
     * 캠페인 종류 ex. GIVEAWAY / CONTENTS / EXCLUSIVE
     */
    @Enumerated(value = EnumType.STRING)
    private CampaignType campaignType;

    /**
     * 캠페인 상태 ex. 임시 저장 / 대기 중 / 오픈 예정 / 진행 중 / 종료
     */
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus campaignStatus;

    /**
     * 캠페인을 진행할 상품의 카테고리
     */
    @Enumerated(value = EnumType.STRING)
    private CampaignProductType campaignProductType;

    private Instant applyStartDate;

    private Instant applyDeadline;

    private Instant creatorAnnouncementDate;

    private Instant reviewSubmissionDeadline;

    private Integer recruitmentNumber; // 모집 인원

    private int applicantNumber; // 지원 인원

    private int approvedNumber; // 승인 인원

    /**
     * 크리에이터 참여 보상 목록
     */
    @ElementCollection
    @CollectionTable(
            name = "campaign_participation_rewards",
            joinColumns = @JoinColumn(name = "campaign_id")
    )
    private List<String> participationRewards;

    /**
     * 크리에이터 제출 콘텐츠 목록
     */
    @ElementCollection
    @CollectionTable(
            name = "campaign_deliverable_requirements",
            joinColumns = @JoinColumn(name = "campaign_id")
    )
    private List<String> deliverableRequirements;

    /**
     * 크리에이터 참여 조건 목록
     */
    @ElementCollection
    @CollectionTable(
            name = "campaign_eligibility_requirements",
            joinColumns = @JoinColumn(name = "campaign_id")
    )
    private List<String> eligibilityRequirements;


    @Column(nullable = false)
    private boolean isPublished = false;

    @Column
    private Instant publishedAt;

    @Enumerated(value = EnumType.STRING)
    private ContentType firstContentPlatform;

    @Enumerated(value = EnumType.STRING)
    private ContentType secondContentPlatform;

    @Builder.Default
    @Column(nullable = false)
    private Integer deleted = 0;

    /**
     * 캠페인 지원자 수 증가 메소드
     */
    public void increaseApplicant() {
        this.applicantNumber += 1;
    }

    /**
     * 승인 인원 수 증가 메소드
     */
    public void increaseApprovedNumber(int toUpdateCount) {
        this.approvedNumber += toUpdateCount;
    }

    /**
     * 캠페인 상태 변경
     *
     * @param newStatus
     */
    public void changeStatus(CampaignStatus newStatus) {
        this.campaignStatus = newStatus;
    }

    public void updateCampaign(CampaignCreateRequest request) {

        if (request.campaignTitle() != null) {
            this.title = request.campaignTitle();
        }
        if (request.language() != null) {
            this.language = request.language();
        }
        if (request.campaignType() != null) {
            this.campaignType = request.campaignType();
        }
        if (request.campaignProductType() != null) {
            this.campaignProductType = request.campaignProductType();
        }
        if (request.applyStartDate() != null) {
            this.applyStartDate = request.applyStartDate();
        }
        if (request.applyDeadline() != null) {
            this.applyDeadline = request.applyDeadline();
        }
        if (request.creatorAnnouncementDate() != null) {
            this.creatorAnnouncementDate = request.creatorAnnouncementDate();
        }
        if (request.reviewSubmissionDeadline() != null) {
            this.reviewSubmissionDeadline = request.reviewSubmissionDeadline();
        }
        if (request.recruitmentNumber() != null) {
            this.recruitmentNumber = request.recruitmentNumber();
        }

        if (request.participationRewards() != null) {
            this.participationRewards.clear();
            this.participationRewards.addAll(request.participationRewards());
        }
        if (request.deliverableRequirements() != null) {
            this.deliverableRequirements.clear();
            this.deliverableRequirements.addAll(request.deliverableRequirements());
        }
        if (request.eligibilityRequirements() != null) {
            this.eligibilityRequirements.clear();
            this.eligibilityRequirements.addAll(request.eligibilityRequirements());
        }
        if (request.firstContentType() != null) {
            this.firstContentPlatform = request.firstContentType();
        }
        if (request.secondContentType() != null) {
            this.secondContentPlatform = request.secondContentType();
        }
    }

    // to be refactored...
    public void updateCampaign(CampaignModifyRequest request) {

        if (request.campaignTitle() != null) {
            this.title = request.campaignTitle();
        }
        if (request.language() != null) {
            this.language = request.language();
        }
        if (request.campaignType() != null) {
            this.campaignType = request.campaignType();
        }
        if (request.campaignProductType() != null) {
            this.campaignProductType = request.campaignProductType();
        }
        if (request.applyStartDate() != null) {
            this.applyStartDate = request.applyStartDate();
        }
        if (request.applyDeadline() != null) {
            this.applyDeadline = request.applyDeadline();
        }
        if (request.creatorAnnouncementDate() != null) {
            this.creatorAnnouncementDate = request.creatorAnnouncementDate();
        }
        if (request.reviewSubmissionDeadline() != null) {
            this.reviewSubmissionDeadline = request.reviewSubmissionDeadline();
        }
        if (request.recruitmentNumber() != null) {
            this.recruitmentNumber = request.recruitmentNumber();
        }

        if (request.participationRewards() != null) {
            this.participationRewards.clear();
            this.participationRewards.addAll(request.participationRewards());
        }
        if (request.deliverableRequirements() != null) {
            this.deliverableRequirements.clear();
            this.deliverableRequirements.addAll(request.deliverableRequirements());
        }
        if (request.eligibilityRequirements() != null) {
            this.eligibilityRequirements.clear();
            this.eligibilityRequirements.addAll(request.eligibilityRequirements());
        }
        if (request.firstContentType() != null) {
            this.firstContentPlatform = request.firstContentType();
        }
        if (request.secondContentType() != null) {
            this.secondContentPlatform = request.secondContentType();
        }
    }


    /**
     * 캠페인 발행 처리
     */
    public void publish() {
        this.isPublished = true;
        this.publishedAt = Instant.now();
        this.campaignStatus = CampaignStatus.WAITING_APPROVAL;
    }

    public void validatePublishable() {
        if (isDraft()) {
            throw new DraftNotFilledException();
        }
    }

    /**
     * 임시 저장 여부 반환 필수 필드 중 하나라도 비어있으면 임시저장으로 간주.
     *
     * @return 임시저장 여부
     */
    public boolean isDraft() {
        return Stream.of(
                this.brand == null,
                this.title == null || this.title.isBlank(),
                this.language == null,
                this.campaignType == null,
                this.campaignProductType == null,
                this.applyStartDate == null,
                this.applyDeadline == null,
                this.creatorAnnouncementDate == null,
                this.reviewSubmissionDeadline == null,
                this.recruitmentNumber == null,
                this.participationRewards == null || this.participationRewards.isEmpty(),
                this.deliverableRequirements == null || this.deliverableRequirements.isEmpty(),
                this.firstContentPlatform == null
        ).anyMatch(condition -> condition);
    }

    public static Campaign createCampaign(CampaignCreateRequest request, Brand brand) {

        return Campaign.builder()
                .brand(brand)
                .title(request.campaignTitle())
                .language(request.language())
                .campaignType(request.campaignType())
                .campaignStatus(CampaignStatus.DRAFT) // DRAFT 가 기본값
                .campaignProductType(request.campaignProductType())
                .applyStartDate(request.applyStartDate())
                .applyDeadline(request.applyDeadline())
                .creatorAnnouncementDate(request.creatorAnnouncementDate())
                .reviewSubmissionDeadline(request.reviewSubmissionDeadline())
                .recruitmentNumber(request.recruitmentNumber())
                .participationRewards(request.participationRewards())
                .deliverableRequirements(request.deliverableRequirements())
                .eligibilityRequirements(request.eligibilityRequirements())
                .firstContentPlatform(request.firstContentType())
                .secondContentPlatform(request.secondContentType())
                .build();
    }
}
