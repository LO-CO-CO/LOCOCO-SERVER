package com.lokoko.domain.scheduler.application.service;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.repository.CampaignRepository;
import com.lokoko.domain.campaign.exception.CampaignNotFoundException;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewRepository;
import com.lokoko.domain.creator.exception.CreatorCampaignNotFoundException;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.domain.scheduler.domain.entity.ScheduledEvent;
import com.lokoko.domain.scheduler.domain.enums.EventType;
import com.lokoko.domain.scheduler.domain.enums.TargetType;
import com.lokoko.domain.scheduler.domain.repository.ScheduledEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EventExecutionService {

    private final CampaignRepository campaignRepository;
    private final CreatorCampaignRepository creatorCampaignRepository;
    private final ScheduledEventRepository scheduledEventRepository;

    // CampaignReview 관련 Repository 추가
    private final CampaignReviewRepository campaignReviewRepository;

    /**
     * 스케줄된 이벤트 실행
     */
    public void execute(ScheduledEvent event) {
        switch (event.getEventType()) {
            // 1. 캠페인 모집 시작
            case CAMPAIGN_START_RECRUITING:
                handleCampaignStartRecruiting(event.getTargetId());
                break;

            // 2. 캠페인 모집 마감
            case CAMPAIGN_CLOSE_RECRUITMENT:
                handleCampaignCloseRecruitment(event.getTargetId());
                break;

            // 3. 크리에이터 발표 (미승인자 REJECTED 처리)
            case CREATOR_ANNOUNCEMENT_PROCESS:
                handleCreatorAnnouncement(event.getTargetId());
                break;

            // 4. 배송지 입력 마감 (24시간 후)
            case CREATOR_ADDRESS_DEADLINE:
                handleAddressDeadline(event.getTargetId());
                break;

            // 5. 캠페인 리뷰 시작 (크리에이터 발표와 동시)
            case CAMPAIGN_START_REVIEW:
                handleCampaignStartReview(event.getTargetId());
                break;

            // 6. 1차 리뷰 제출 마감 (리뷰 마감 7일 전)
            case CREATOR_FIRST_REVIEW_DEADLINE:
                handleFirstReviewDeadline(event.getTargetId());
                break;

            // 7. 2차 리뷰 제출 마감 (최종 마감일)
            case CREATOR_SECOND_REVIEW_DEADLINE:
                handleSecondReviewDeadline(event.getTargetId());
                break;

            // 8. 캠페인 완료
            case CAMPAIGN_COMPLETE:
                handleCampaignComplete(event.getTargetId());
                break;

            default:
                throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
        }
    }

    /**
     * 캠페인 모집 시작 처리
     */
    private void handleCampaignStartRecruiting(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        if (campaign.getCampaignStatus() == CampaignStatus.OPEN_RESERVED) {
            campaign.changeStatus(CampaignStatus.RECRUITING);
        }
    }

    /**
     * 캠페인 모집 마감 처리
     */
    private void handleCampaignCloseRecruitment(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        if (campaign.getCampaignStatus() == CampaignStatus.RECRUITING) {
            campaign.changeStatus(CampaignStatus.RECRUITMENT_CLOSED);
        }
    }

    /**
     * 캠페인 리뷰 시작 처리
     */
    private void handleCampaignStartReview(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        if (campaign.getCampaignStatus() == CampaignStatus.RECRUITMENT_CLOSED) {
            campaign.changeStatus(CampaignStatus.IN_REVIEW);
        }
    }

    /**
     * 캠페인 완료 처리
     */
    private void handleCampaignComplete(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        if (campaign.getCampaignStatus() == CampaignStatus.IN_REVIEW) {
            campaign.changeStatus(CampaignStatus.COMPLETED);
        }
    }

    /**
     * 크리에이터 발표 처리
     * - 캠페인 상태를 IN_REVIEW로 변경
     * - 승인되지 않은 크리에이터들을 REJECTED로 변경
     * - 승인된 크리에이터들의 배송지 입력 마감 이벤트 등록
     */
    private void handleCreatorAnnouncement(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        // 캠페인 상태 변경
        if (campaign.getCampaignStatus() == CampaignStatus.RECRUITMENT_CLOSED) {
            campaign.changeStatus(CampaignStatus.IN_REVIEW);
        }

        // PENDING 상태의 크리에이터를 모두 REJECTED로 변경
        List<CreatorCampaign> pendingCreators = creatorCampaignRepository
                .findByCampaignIdAndStatus(campaignId, ParticipationStatus.PENDING);

        for (CreatorCampaign creatorCampaign : pendingCreators) {
            creatorCampaign.changeStatus(ParticipationStatus.REJECTED);
        }

        // APPROVED 상태의 크리에이터들에 대해 배송지 입력 마감 이벤트 등록
        List<CreatorCampaign> approvedCreators = creatorCampaignRepository
                .findByCampaignIdAndStatus(campaignId, ParticipationStatus.APPROVED);

        Instant addressDeadline = Instant.now().plus(Duration.ofHours(24));
        for (CreatorCampaign creatorCampaign : approvedCreators) {
            ScheduledEvent addressEvent = ScheduledEvent.builder()
                    .eventType(EventType.CREATOR_ADDRESS_DEADLINE)
                    .targetId(creatorCampaign.getId())
                    .targetType(TargetType.CREATOR_CAMPAIGN)
                    .executeAt(addressDeadline)
                    .build();
            scheduledEventRepository.save(addressEvent);
        }
    }

    /**
     * 1차 리뷰 제출 마감 처리
     * - 리뷰 제출 마감 7일 전까지 미제출한 크리에이터를 EXPIRED로 변경
     */
    private void handleFirstReviewDeadline(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        // ACTIVE 상태의 크리에이터들을 조회
        List<CreatorCampaign> activeCreators = creatorCampaignRepository
                .findByCampaignIdAndStatus(campaignId, ParticipationStatus.ACTIVE);

        for (CreatorCampaign creatorCampaign : activeCreators) {
            if (!hasSubmittedFirstReview(creatorCampaign, campaign)) {
                creatorCampaign.changeStatus(ParticipationStatus.EXPIRED);
            }
        }
    }

    /**
     * 2차 리뷰 제출 마감 처리
     * - 리뷰 제출 마감일까지 미제출한 크리에이터를 EXPIRED로 변경
     */
    private void handleSecondReviewDeadline(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        // ACTIVE 상태의 크리에이터들을 조회
        List<CreatorCampaign> activeCreators = creatorCampaignRepository
                .findByCampaignIdAndStatus(campaignId, ParticipationStatus.ACTIVE);

        for (CreatorCampaign creatorCampaign : activeCreators) {
            if (!hasSubmittedSecondReview(creatorCampaign, campaign)) {
                creatorCampaign.changeStatus(ParticipationStatus.EXPIRED);
            }
        }
    }

    /**
     * 배송지 입력 마감 처리
     * - 발표 후 24시간 내에 배송지를 확인하지 않은 크리에이터를 EXPIRED로 변경
     */
    private void handleAddressDeadline(Long creatorCampaignId) {
        CreatorCampaign creatorCampaign = creatorCampaignRepository.findById(creatorCampaignId)
                .orElseThrow(CreatorCampaignNotFoundException::new);

        // 배송지 미확인 && APPROVED 상태인 경우 EXPIRED로 변경
        if (creatorCampaign.getStatus() == ParticipationStatus.APPROVED &&
                (creatorCampaign.getAddressConfirmed() == null || !creatorCampaign.getAddressConfirmed())) {
            creatorCampaign.changeStatus(ParticipationStatus.EXPIRED);
        }
    }

    /**
     * 1차 리뷰 제출 여부 확인
     * 캠페인에 설정된 모든 콘텐츠 타입에 대해 제출되었는지 확인
     */
    private boolean hasSubmittedFirstReview(CreatorCampaign creatorCampaign, Campaign campaign) {
        List<ContentType> requiredContentTypes = getRequiredContentTypes(campaign);

        for (ContentType contentType : requiredContentTypes) {
            boolean hasSubmitted = campaignReviewRepository.existsByCreatorCampaignIdAndReviewRoundAndContentType(
                    creatorCampaign.getId(),
                    ReviewRound.FIRST,
                    contentType
            );

            if (!hasSubmitted) return false;
        }
        return true;
    }

    /**
     * 2차 리뷰 제출 여부 확인
     * 캠페인에 설정된 모든 콘텐츠 타입에 대해 제출되었는지 확인
     */
    private boolean hasSubmittedSecondReview(CreatorCampaign creatorCampaign, Campaign campaign) {
        List<ContentType> requiredContentTypes = getRequiredContentTypes(campaign);

        for (ContentType contentType : requiredContentTypes) {
            boolean hasSubmitted = campaignReviewRepository.existsByCreatorCampaignIdAndReviewRoundAndContentType(
                    creatorCampaign.getId(),
                    ReviewRound.SECOND,
                    contentType
            );

            if (!hasSubmitted) return false;
        }
        return true;
    }

    /**
     * 캠페인에 설정된 필수 콘텐츠 타입 목록 반환
     * firstContentPlatform, secondContentPlatform 설정에 따라 1~2개 반환
     */
    private List<ContentType> getRequiredContentTypes(Campaign campaign) {
        List<ContentType> contentTypes = new ArrayList<>();

        if (campaign.getFirstContentPlatform() != null) {
            contentTypes.add(campaign.getFirstContentPlatform());
        }

        if (campaign.getSecondContentPlatform() != null &&
            !campaign.getSecondContentPlatform().equals(campaign.getFirstContentPlatform())) {
            contentTypes.add(campaign.getSecondContentPlatform());
        }

        return contentTypes;
    }
}