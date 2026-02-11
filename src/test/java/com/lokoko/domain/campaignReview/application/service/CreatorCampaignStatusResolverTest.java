package com.lokoko.domain.campaignReview.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;

@DisplayName("CreatorCampaign 상태 계산")
class CreatorCampaignStatusResolverTest {

    private final CreatorCampaignStatusResolver resolver = new CreatorCampaignStatusResolver();

    @Test
    @DisplayName("determineNextStatus() : 2차 리뷰만 있어도 1차 리뷰가 없으면 완료 처리하지 않는다")
    void determineNextStatus_doesNotCompleteWithoutFirstReview() {
        CreatorCampaign creatorCampaign = CreatorCampaign.builder()
                .addressConfirmed(true)
                .status(ParticipationStatus.ACTIVE)
                .build();
        Campaign campaign = Campaign.builder()
                .firstContentPlatform(ContentType.INSTA_REELS)
                .build();

        ParticipationStatus result = resolver.determineNextStatus(
                creatorCampaign,
                campaign,
                false,
                false,
                true,
                List.of()
        );

        assertThat(result).isEqualTo(ParticipationStatus.ACTIVE);
    }
}
