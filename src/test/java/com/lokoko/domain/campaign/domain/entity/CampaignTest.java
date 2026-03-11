package com.lokoko.domain.campaign.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductType;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.domain.campaign.exception.AdminCampaignNotModifiableException;
import com.lokoko.domain.campaign.exception.CampaignCapacityExceedException;
import com.lokoko.domain.campaign.exception.CampaignExpiredException;
import com.lokoko.domain.campaign.exception.DraftNotFilledException;
import com.lokoko.domain.campaign.exception.CampaignNotEditableException;
import com.lokoko.domain.creatorCampaign.exception.CampaignNotRecruitingException;
import com.lokoko.domain.creatorCampaign.exception.CampaignNotStartedException;
import com.lokoko.domain.creatorCampaign.exception.CampaignRecruitmentFullException;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.domain.user.exception.CampaignApprovalNotAllowedException;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("캠페인 도메인 검증")
class CampaignTest {

    @Test
    @DisplayName("isDraft() : 발행 필수값이 모두 있으면 brand 연관관계가 없어도 draft가 아니다")
    void isDraft_ignoresMissingBrandRelation() {
        Campaign campaign = completeCampaignBuilder()
                .brand(null)
                .brandName(null)
                .build();

        assertThat(campaign.isDraft()).isFalse();
    }

    @Test
    @DisplayName("validatePublishable() : 필수 필드가 비어 있으면 예외를 던진다")
    void validatePublishable_throwsWhenRequiredFieldMissing() {
        Campaign campaign = completeCampaignBuilder()
                .firstContentPlatform(null)
                .build();

        assertThatThrownBy(campaign::validatePublishable)
                .isInstanceOf(DraftNotFilledException.class);
    }

    @Test
    @DisplayName("validatePublishableForAdmin() : brandName 이 있으면 brand 연관관계 없이도 통과한다")
    void validatePublishableForAdmin_allowsBrandNameWithoutBrandRelation() {
        Campaign campaign = completeCampaignBuilder()
                .brand(null)
                .brandName("Lokoko")
                .build();

        assertThatCode(campaign::validatePublishableForAdmin)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validatePublishableForAdmin() : 어드민 캠페인은 brandName 이 필수다")
    void validatePublishableForAdmin_requiresBrandName() {
        Campaign campaign = completeCampaignBuilder()
                .brand(null)
                .brandName(" ")
                .build();

        assertThatThrownBy(campaign::validatePublishableForAdmin)
                .isInstanceOf(DraftNotFilledException.class);
    }

    @Test
    @DisplayName("publish() : 캠페인을 발행 상태와 승인 대기 상태로 변경한다")
    void publish_marksCampaignAsWaitingApproval() {
        Campaign campaign = completeCampaignBuilder()
                .campaignStatus(CampaignStatus.DRAFT)
                .build();

        campaign.publish();

        assertThat(campaign.isPublished()).isTrue();
        assertThat(campaign.getCampaignStatus()).isEqualTo(CampaignStatus.WAITING_APPROVAL);
        assertThat(campaign.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("validateParticipatableAt() : 신청 시작 전이면 예외를 던진다")
    void validateParticipatableAt_rejectsBeforeStart() {
        Instant now = Instant.parse("2026-03-11T00:00:00Z");
        Campaign campaign = completeCampaignBuilder()
                .campaignStatus(CampaignStatus.RECRUITING)
                .applyStartDate(now.plusSeconds(60))
                .build();

        assertThatThrownBy(() -> campaign.validateParticipatableAt(now))
                .isInstanceOf(CampaignNotStartedException.class);
    }

    @Test
    @DisplayName("validateParticipatableAt() : 신청 마감 후면 예외를 던진다")
    void validateParticipatableAt_rejectsAfterDeadline() {
        Instant now = Instant.parse("2026-03-11T00:00:00Z");
        Campaign campaign = completeCampaignBuilder()
                .campaignStatus(CampaignStatus.RECRUITING)
                .applyStartDate(now.minusSeconds(60))
                .applyDeadline(now.minusSeconds(1))
                .build();

        assertThatThrownBy(() -> campaign.validateParticipatableAt(now))
                .isInstanceOf(CampaignExpiredException.class);
    }

    @Test
    @DisplayName("validateParticipatableAt() : 모집 중 상태가 아니면 예외를 던진다")
    void validateParticipatableAt_requiresRecruitingStatus() {
        Instant now = Instant.parse("2026-03-11T00:00:00Z");
        Campaign campaign = completeCampaignBuilder()
                .applyStartDate(now.minusSeconds(60))
                .applyDeadline(now.plusSeconds(3600))
                .campaignStatus(CampaignStatus.OPEN_RESERVED)
                .build();

        assertThatThrownBy(() -> campaign.validateParticipatableAt(now))
                .isInstanceOf(CampaignNotRecruitingException.class);
    }

    @Test
    @DisplayName("validateParticipatableAt() : 모집 인원이 가득 찼으면 예외를 던진다")
    void validateParticipatableAt_rejectsFullRecruitment() {
        Instant now = Instant.parse("2026-03-11T00:00:00Z");
        Campaign campaign = completeCampaignBuilder()
                .applyStartDate(now.minusSeconds(60))
                .applyDeadline(now.plusSeconds(3600))
                .campaignStatus(CampaignStatus.RECRUITING)
                .approvedNumber(5)
                .recruitmentNumber(5)
                .build();

        assertThatThrownBy(() -> campaign.validateParticipatableAt(now))
                .isInstanceOf(CampaignRecruitmentFullException.class);
    }

    @Test
    @DisplayName("approveByAdmin() : 시작일이 미래면 OPEN_RESERVED 로 변경한다")
    void approveByAdmin_setsOpenReservedForFutureCampaign() {
        Instant now = Instant.parse("2026-03-11T00:00:00Z");
        Campaign campaign = completeCampaignBuilder()
                .campaignStatus(CampaignStatus.WAITING_APPROVAL)
                .applyStartDate(now.plusSeconds(60))
                .build();

        campaign.approveByAdmin(now);

        assertThat(campaign.getCampaignStatus()).isEqualTo(CampaignStatus.OPEN_RESERVED);
    }

    @Test
    @DisplayName("approveByAdmin() : 이미 시작된 캠페인은 RECRUITING 으로 변경한다")
    void approveByAdmin_setsRecruitingForStartedCampaign() {
        Instant now = Instant.parse("2026-03-11T00:00:00Z");
        Campaign campaign = completeCampaignBuilder()
                .campaignStatus(CampaignStatus.WAITING_APPROVAL)
                .applyStartDate(now.minusSeconds(60))
                .build();

        campaign.approveByAdmin(now);

        assertThat(campaign.getCampaignStatus()).isEqualTo(CampaignStatus.RECRUITING);
    }

    @Test
    @DisplayName("approveByAdmin() : WAITING_APPROVAL 이 아니면 예외를 던진다")
    void approveByAdmin_rejectsUnexpectedStatus() {
        Campaign campaign = completeCampaignBuilder()
                .campaignStatus(CampaignStatus.RECRUITING)
                .build();

        assertThatThrownBy(() -> campaign.approveByAdmin(Instant.parse("2026-03-11T00:00:00Z")))
                .isInstanceOf(CampaignApprovalNotAllowedException.class);
    }

    @Test
    @DisplayName("validateAdminModifiable() : WAITING_APPROVAL 상태에서만 수정 가능하다")
    void validateAdminModifiable_onlyAllowsWaitingApproval() {
        Campaign campaign = completeCampaignBuilder()
                .campaignStatus(CampaignStatus.RECRUITING)
                .build();

        assertThatThrownBy(campaign::validateAdminModifiable)
                .isInstanceOf(AdminCampaignNotModifiableException.class);
    }

    @Test
    @DisplayName("validateEditable() : 이미 발행된 캠페인이면 예외를 던진다")
    void validateEditable_rejectsPublishedCampaign() {
        Campaign campaign = completeCampaignBuilder()
                .isPublished(true)
                .build();

        assertThatThrownBy(campaign::validateEditable)
                .isInstanceOf(CampaignNotEditableException.class);
    }

    @Test
    @DisplayName("validateCapacityForApproval() : 승인 요청 수가 모집 인원을 넘으면 예외를 던진다")
    void validateCapacityForApproval_rejectsExceedingRecruitmentNumber() {
        Campaign campaign = completeCampaignBuilder()
                .approvedNumber(4)
                .recruitmentNumber(5)
                .build();

        assertThatThrownBy(() -> campaign.validateCapacityForApproval(2))
                .isInstanceOf(CampaignCapacityExceedException.class);
    }

    private Campaign.CampaignBuilder<?, ?> completeCampaignBuilder() {
        Instant now = Instant.parse("2026-03-11T00:00:00Z");

        return Campaign.builder()
                .brandName("Lokoko")
                .title("Spring Campaign")
                .language(CampaignLanguage.EN)
                .campaignType(CampaignType.CONTENTS)
                .campaignStatus(CampaignStatus.DRAFT)
                .campaignProductType(CampaignProductType.SKINCARE)
                .applyStartDate(now.plusSeconds(60))
                .applyDeadline(now.plusSeconds(3600))
                .creatorAnnouncementDate(now.plusSeconds(7200))
                .reviewSubmissionDeadline(now.plusSeconds(10800))
                .recruitmentNumber(5)
                .participationRewards(List.of("Reward"))
                .deliverableRequirements(List.of("Deliverable"))
                .eligibilityRequirements(List.of("Eligibility"))
                .firstContentPlatform(ContentType.INSTA_REELS);
    }
}
