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

@DisplayName("Campaign domain validation")
class CampaignTest {

    @Test
    @DisplayName("isDraft() ignores missing brand relation when required publish fields are present")
    void isDraft_ignoresMissingBrandRelation() {
        Campaign campaign = completeCampaignBuilder()
                .brand(null)
                .brandName(null)
                .build();

        assertThat(campaign.isDraft()).isFalse();
    }

    @Test
    @DisplayName("validatePublishable() throws when a required brand-campaign field is missing")
    void validatePublishable_throwsWhenRequiredFieldMissing() {
        Campaign campaign = completeCampaignBuilder()
                .firstContentPlatform(null)
                .build();

        assertThatThrownBy(campaign::validatePublishable)
                .isInstanceOf(DraftNotFilledException.class);
    }

    @Test
    @DisplayName("validatePublishableForAdmin() allows admin campaign without brand relation when brandName exists")
    void validatePublishableForAdmin_allowsBrandNameWithoutBrandRelation() {
        Campaign campaign = completeCampaignBuilder()
                .brand(null)
                .brandName("Lokoko")
                .build();

        assertThatCode(campaign::validatePublishableForAdmin)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validatePublishableForAdmin() requires brandName for admin-created campaigns")
    void validatePublishableForAdmin_requiresBrandName() {
        Campaign campaign = completeCampaignBuilder()
                .brand(null)
                .brandName(" ")
                .build();

        assertThatThrownBy(campaign::validatePublishableForAdmin)
                .isInstanceOf(DraftNotFilledException.class);
    }

    @Test
    @DisplayName("publish() marks the campaign as published and waiting approval")
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
    @DisplayName("validateParticipatableAt() rejects campaigns before the application window opens")
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
    @DisplayName("validateParticipatableAt() rejects campaigns after the deadline")
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
    @DisplayName("validateParticipatableAt() requires recruiting status")
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
    @DisplayName("validateParticipatableAt() rejects full recruitment")
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
    @DisplayName("approveByAdmin() moves campaigns with a future start to OPEN_RESERVED")
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
    @DisplayName("approveByAdmin() moves already-open campaigns to RECRUITING")
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
    @DisplayName("approveByAdmin() rejects campaigns outside WAITING_APPROVAL")
    void approveByAdmin_rejectsUnexpectedStatus() {
        Campaign campaign = completeCampaignBuilder()
                .campaignStatus(CampaignStatus.RECRUITING)
                .build();

        assertThatThrownBy(() -> campaign.approveByAdmin(Instant.parse("2026-03-11T00:00:00Z")))
                .isInstanceOf(CampaignApprovalNotAllowedException.class);
    }

    @Test
    @DisplayName("validateAdminModifiable() only allows WAITING_APPROVAL campaigns")
    void validateAdminModifiable_onlyAllowsWaitingApproval() {
        Campaign campaign = completeCampaignBuilder()
                .campaignStatus(CampaignStatus.RECRUITING)
                .build();

        assertThatThrownBy(campaign::validateAdminModifiable)
                .isInstanceOf(AdminCampaignNotModifiableException.class);
    }

    @Test
    @DisplayName("validateEditable() rejects already published campaigns")
    void validateEditable_rejectsPublishedCampaign() {
        Campaign campaign = completeCampaignBuilder()
                .isPublished(true)
                .build();

        assertThatThrownBy(campaign::validateEditable)
                .isInstanceOf(CampaignNotEditableException.class);
    }

    @Test
    @DisplayName("validateCapacityForApproval() rejects approvals exceeding recruitment number")
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
