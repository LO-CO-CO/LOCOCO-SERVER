package com.lokoko.domain.campaignReview.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.config.BetaFeatureConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreatorCampaign 상태 갱신")
class CreatorCampaignUpdateServiceTest {

    @Mock
    private CreatorCampaignRepository creatorCampaignRepository;

    @Mock
    private CampaignReviewGetService campaignReviewGetService;

    @Mock
    private BetaFeatureConfig betaFeatureConfig;

    @Mock
    private CreatorCampaignStatusResolver creatorCampaignStatusResolver;

    @InjectMocks
    private CreatorCampaignUpdateService creatorCampaignUpdateService;

    @Test
    @DisplayName("refreshParticipationStatus() : 배송지 확인 전까지는 APPROVED 로 유지한다")
    void refreshParticipationStatus_requiresConfirmedAddressFirst() {
        CreatorCampaign creatorCampaign = creatorCampaign(false, ParticipationStatus.ACTIVE, singlePlatformCampaign());

        given(creatorCampaignRepository.getByIdForUpdate(1L)).willReturn(creatorCampaign);
        given(campaignReviewGetService.existsFirst(1L)).willReturn(true);
        given(campaignReviewGetService.existsSecond(1L)).willReturn(true);
        given(campaignReviewGetService.findContentTypesByRound(eq(1L), any(ReviewRound.class)))
                .willReturn(List.of(ContentType.INSTA_REELS));
        given(creatorCampaignStatusResolver.determineNextStatus(
                eq(creatorCampaign),
                eq(creatorCampaign.getCampaign()),
                eq(false),
                eq(true),
                eq(true),
                any(List.class)
        )).willReturn(ParticipationStatus.APPROVED);

        creatorCampaignUpdateService.refreshParticipationStatus(1L);

        assertThat(creatorCampaign.getStatus()).isEqualTo(ParticipationStatus.APPROVED);
    }

    @Test
    @DisplayName("refreshParticipationStatus() : 베타 플로우에서는 필요한 1차 리뷰가 모두 있어야 완료 처리한다")
    void refreshParticipationStatus_completesBetaFlowWhenAllFirstReviewsExist() {
        CreatorCampaign creatorCampaign = creatorCampaign(true, ParticipationStatus.ACTIVE, dualPlatformCampaign());

        given(creatorCampaignRepository.getByIdForUpdate(1L)).willReturn(creatorCampaign);
        given(campaignReviewGetService.existsFirst(1L)).willReturn(true);
        given(campaignReviewGetService.existsSecond(1L)).willReturn(false);
        given(betaFeatureConfig.isSimplifiedReviewFlow()).willReturn(true);
        given(campaignReviewGetService.findContentTypesByRound(eq(1L), any(ReviewRound.class)))
                .willReturn(List.of(ContentType.INSTA_REELS, ContentType.TIKTOK_VIDEO));
        given(creatorCampaignStatusResolver.determineNextStatus(
                eq(creatorCampaign),
                eq(creatorCampaign.getCampaign()),
                eq(true),
                eq(true),
                eq(false),
                any(List.class)
        )).willReturn(ParticipationStatus.COMPLETED);

        creatorCampaignUpdateService.refreshParticipationStatus(1L);

        assertThat(creatorCampaign.getStatus()).isEqualTo(ParticipationStatus.COMPLETED);
    }

    @Test
    @DisplayName("refreshParticipationStatus() : 일반 플로우에서는 2차 리뷰가 있으면 완료 처리한다")
    void refreshParticipationStatus_marksCompletedWhenSecondReviewExists() {
        CreatorCampaign creatorCampaign = creatorCampaign(true, ParticipationStatus.ACTIVE, singlePlatformCampaign());

        given(creatorCampaignRepository.getByIdForUpdate(1L)).willReturn(creatorCampaign);
        given(campaignReviewGetService.existsFirst(1L)).willReturn(true);
        given(campaignReviewGetService.existsSecond(1L)).willReturn(true);
        given(betaFeatureConfig.isSimplifiedReviewFlow()).willReturn(false);
        given(campaignReviewGetService.findContentTypesByRound(eq(1L), any(ReviewRound.class)))
                .willReturn(List.of(ContentType.INSTA_REELS));
        given(creatorCampaignStatusResolver.determineNextStatus(
                eq(creatorCampaign),
                eq(creatorCampaign.getCampaign()),
                eq(false),
                eq(true),
                eq(true),
                any(List.class)
        )).willReturn(ParticipationStatus.COMPLETED);

        creatorCampaignUpdateService.refreshParticipationStatus(1L);

        assertThat(creatorCampaign.getStatus()).isEqualTo(ParticipationStatus.COMPLETED);
    }

    private CreatorCampaign creatorCampaign(boolean addressConfirmed,
                                            ParticipationStatus status,
                                            Campaign campaign) {
        return CreatorCampaign.builder()
                .campaign(campaign)
                .status(status)
                .addressConfirmed(addressConfirmed)
                .build();
    }

    private Campaign singlePlatformCampaign() {
        return Campaign.builder()
                .firstContentPlatform(ContentType.INSTA_REELS)
                .build();
    }

    private Campaign dualPlatformCampaign() {
        return Campaign.builder()
                .firstContentPlatform(ContentType.INSTA_REELS)
                .secondContentPlatform(ContentType.TIKTOK_VIDEO)
                .build();
    }
}
