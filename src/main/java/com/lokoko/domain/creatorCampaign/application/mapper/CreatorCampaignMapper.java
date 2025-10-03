package com.lokoko.domain.creatorCampaign.application.mapper;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewRepository;
import com.lokoko.domain.creator.api.dto.response.CreatorBasicInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorMyCampaignListResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorMyCampaignResponse;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.util.CampaignStatusMapper;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.image.domain.repository.CampaignImageRepository;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.common.response.PageableResponse;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatorCampaignMapper {

    private final CampaignReviewRepository campaignReviewRepository;
    private final CampaignImageRepository campaignImageRepository;

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
        Campaign campaign = participation.getCampaign();

        // 컨텐츠 타입 정보 수집
        List<ContentType> requiredContentTypes = new java.util.ArrayList<>();
        if (campaign.getFirstContentPlatform() != null) {
            requiredContentTypes.add(campaign.getFirstContentPlatform());
        }
        if (campaign.getSecondContentPlatform() != null) {
            requiredContentTypes.add(campaign.getSecondContentPlatform());
        }

        // 캠페인 대표 이미지 조회 (썸네일 타입의 첫 번째 이미지)
        String campaignImageUrl = campaignImageRepository.findThumbnailImagesByCampaignId(campaign.getId())
                .stream()
                .findFirst()
                .map(com.lokoko.domain.campaign.api.dto.response.CampaignImageResponse::url)
                .orElse(null);

        // 실제 리뷰 정보 조회
        List<CampaignReview> campaignReviews = campaignReviewRepository.findAllByCreatorCampaignIdOrderByIdAsc(participation.getId());

        // CampaignReview를 CampaignStatusMapper.ReviewInfo로 변환
        List<CampaignStatusMapper.ReviewInfo> reviewInfos = campaignReviews.stream()
                .map(review -> new CampaignStatusMapper.ReviewInfo(
                        review.getId(),
                        review.getReviewRound(),
                        review.getContentType(),
                        review.getStatus(),
                        review.getCreatedAt() != null
                            ? review.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant()
                            : null,
                        review.isNoteViewed()
                ))
                .collect(java.util.stream.Collectors.toList());

        // creatorAnnouncementDate 이전이면 PENDING으로 표시
        ParticipationStatus displayStatus = calculateDisplayStatus(participation, campaign);

        return CreatorMyCampaignResponse.of(
                campaign.getId(),
                campaign.getTitle(),
                campaignImageUrl,
                campaign.getReviewSubmissionDeadline(),
                displayStatus,
                reviewInfos, // 실제 리뷰 정보
                requiredContentTypes
        );
    }

    /**
     * 크리에이터에게 보여줄 실제 상태 계산
     * APPROVED 상태라도 creatorAnnouncementDate 이전이면 PENDING으로 표시
     */
    private ParticipationStatus calculateDisplayStatus(CreatorCampaign participation, Campaign campaign) {
        // 기본적으로는 실제 상태를 반환
        ParticipationStatus actualStatus = participation.getStatus();

        // APPROVED 상태이고 creatorAnnouncementDate가 아직 지나지 않았다면 PENDING으로 표시
        if (actualStatus == ParticipationStatus.APPROVED
            && campaign.getCreatorAnnouncementDate() != null
            && Instant.now().isBefore(campaign.getCreatorAnnouncementDate())) {
            return ParticipationStatus.PENDING;
        }

        return actualStatus;
    }

    /**
     * 리뷰 정보를 포함한 상세 응답 생성 (향후 확장용)
     */
    public CreatorMyCampaignResponse toMyCampaignResponseWithReviews(
            CreatorCampaign participation,
            List<CampaignStatusMapper.ReviewInfo> reviews) {

        Campaign campaign = participation.getCampaign();

        // 컨텐츠 타입 정보 수집
        List<ContentType> requiredContentTypes = new java.util.ArrayList<>();
        if (campaign.getFirstContentPlatform() != null) {
            requiredContentTypes.add(campaign.getFirstContentPlatform());
        }
        if (campaign.getSecondContentPlatform() != null) {
            requiredContentTypes.add(campaign.getSecondContentPlatform());
        }

        // 캠페인 대표 이미지 조회 (썸네일 타입의 첫 번째 이미지)
        String campaignImageUrl = campaignImageRepository.findThumbnailImagesByCampaignId(campaign.getId())
                .stream()
                .findFirst()
                .map(com.lokoko.domain.campaign.api.dto.response.CampaignImageResponse::url)
                .orElse(null);

        return CreatorMyCampaignResponse.of(
                campaign.getId(),
                campaign.getTitle(),
                campaignImageUrl,
                campaign.getReviewSubmissionDeadline(),
                participation.getStatus(),
                reviews,
                requiredContentTypes
        );
    }

    public CreatorMyCampaignListResponse toMyCampaignListResponse(Creator creator, List<CreatorMyCampaignResponse> campaigns,
                                                                  Slice<?> slice) {
        return CreatorMyCampaignListResponse.builder()
                .basicInfo(toBasicInfo(creator))
                .campaigns(campaigns)
                .pageInfo(PageableResponse.of(slice))
                .build();
    }

    /**
     * totalPages 정보를 포함한 CreatorMyCampaignListResponse 생성
     */
    public CreatorMyCampaignListResponse toMyCampaignListResponse(Creator creator, List<CreatorMyCampaignResponse> campaigns,
                                                                  Slice<?> slice, long totalElements) {
        return CreatorMyCampaignListResponse.builder()
                .basicInfo(toBasicInfo(creator))
                .campaigns(campaigns)
                .pageInfo(PageableResponse.of(
                        slice.getNumber(),
                        slice.getSize(),
                        slice.getNumberOfElements(),
                        slice.isLast(),
                        totalElements
                ))
                .build();
    }

    private CreatorBasicInfo toBasicInfo(Creator creator) {
        return CreatorBasicInfo.builder()
                .creatorId(creator.getId())
                .profileImageUrl(creator.getUser() != null ? creator.getUser().getProfileImageUrl() : null)
                .creatorName(creator.getCreatorName())
                .firstName(creator.getFirstName())
                .lastName(creator.getLastName())
                .gender(creator.getGender())
                .birthDate(creator.getBirthDate())
                .email(creator.getUser() != null ? creator.getUser().getEmail() : null)
                .creatorLevel(creator.getCreatorType() != null ? creator.getCreatorType().name() : null)
                .build();
    }
}
