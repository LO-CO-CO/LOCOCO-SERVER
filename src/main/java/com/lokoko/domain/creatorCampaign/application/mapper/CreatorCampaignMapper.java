package com.lokoko.domain.creatorCampaign.application.mapper;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.creator.api.dto.response.CreatorBasicInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorMyCampaignListResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorMyCampaignResponse;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.global.common.response.PageableResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class CreatorCampaignMapper {

    public CreatorCampaign toCampaignParticipation(Creator creator, Campaign campaign, Instant now) {
        return CreatorCampaign.builder()
                .creator(creator)
                .campaign(campaign)
                .status(ParticipationStatus.PENDING)
                .appliedAt(now)
                .addressConfirmed(false)
                .build();
    }

    public CreatorMyCampaignResponse toMyCampaignResponse(CreatorCampaign participation) {
        Campaign c = participation.getCampaign();

        return CreatorMyCampaignResponse.builder()
                .campaignId(c.getId())
                .title(c.getTitle())
                .reviewSubmissionDeadline(c.getReviewSubmissionDeadline())
                .participationStatus(participation.getStatus())
                .build();
    }

    public CreatorMyCampaignListResponse toMyCampaignListResponse(Creator creator, List<CreatorMyCampaignResponse> campaigns,
                                                                  Slice<?> slice) {
        return CreatorMyCampaignListResponse.builder()
                .basicInfo(toBasicInfo(creator))
                .campaigns(campaigns)
                .pageInfo(PageableResponse.of(slice))
                .build();
    }

    private CreatorBasicInfo toBasicInfo(Creator creator) {
        return CreatorBasicInfo.builder()
                .profileImageUrl(creator.getUser() != null ? creator.getUser().getProfileImageUrl() : null)
                .creatorName(creator.getCreatorName())
                .firstName(creator.getFirstName())
                .lastName(creator.getLastName())
                .email(creator.getUser() != null ? creator.getUser().getEmail() : null)
                .creatorLevel(creator.getCreatorType().name())
                .build();
    }
}
