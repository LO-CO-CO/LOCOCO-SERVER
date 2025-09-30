package com.lokoko.domain.scheduler.application.service;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.scheduler.domain.entity.ScheduledEvent;
import com.lokoko.domain.scheduler.domain.enums.EventStatus;
import com.lokoko.domain.scheduler.domain.enums.EventType;
import com.lokoko.domain.scheduler.domain.enums.TargetType;
import com.lokoko.domain.scheduler.domain.repository.ScheduledEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 캠페인 이벤트 스케줄링 서비스
 * 캠페인 생성/수정 시 필요한 스케줄 이벤트를 등록
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CampaignEventScheduler {

    private final ScheduledEventRepository scheduledEventRepository;

    /**
     * 캠페인 승인 시 모든 관련 이벤트 스케줄링
     * @param campaign 승인된 캠페인 (OPEN_RESERVED 또는 RECRUITING 상태)
     */
    public void scheduleCampaignEvents(Campaign campaign) {

        // 기존 PENDING 상태의 이벤트 삭제 (중복 방지)
        scheduledEventRepository.deletePendingEventsByTarget(TargetType.CAMPAIGN, campaign.getId());

        List<ScheduledEvent> events = new ArrayList<>();

        // 1. 모집 시작 이벤트
        if (campaign.getApplyStartDate() != null) {
            events.add(ScheduledEvent.builder()
                    .eventType(EventType.CAMPAIGN_START_RECRUITING)
                    .targetId(campaign.getId())
                    .targetType(TargetType.CAMPAIGN)
                    .executeAt(campaign.getApplyStartDate())
                    .status(EventStatus.PENDING)
                    .build());
        }

        // 2. 모집 마감 이벤트
        if (campaign.getApplyDeadline() != null) {
            events.add(ScheduledEvent.builder()
                    .eventType(EventType.CAMPAIGN_CLOSE_RECRUITMENT)
                    .targetId(campaign.getId())
                    .targetType(TargetType.CAMPAIGN)
                    .executeAt(campaign.getApplyDeadline())
                    .status(EventStatus.PENDING)
                    .build());
        }

        // 3. 크리에이터 발표 및 리뷰 시작 이벤트
        if (campaign.getCreatorAnnouncementDate() != null) {
            events.add(ScheduledEvent.builder()
                    .eventType(EventType.CREATOR_ANNOUNCEMENT_PROCESS)
                    .targetId(campaign.getId())
                    .targetType(TargetType.CAMPAIGN)
                    .executeAt(campaign.getCreatorAnnouncementDate())
                    .status(EventStatus.PENDING)
                    .build());

            // 4. 1차 리뷰 마감 이벤트 (리뷰 제출 마감 7일 전)
            if (campaign.getReviewSubmissionDeadline() != null) {
                events.add(ScheduledEvent.builder()
                        .eventType(EventType.CREATOR_FIRST_REVIEW_DEADLINE)
                        .targetId(campaign.getId())
                        .targetType(TargetType.CAMPAIGN)
                        .executeAt(campaign.getReviewSubmissionDeadline().minus(Duration.ofDays(7)))
                        .status(EventStatus.PENDING)
                        .build());
            }
        }

        // 5. 2차 리뷰 마감 및 캠페인 완료 이벤트
        if (campaign.getReviewSubmissionDeadline() != null) {
            // 2차 리뷰 마감 처리
            events.add(ScheduledEvent.builder()
                    .eventType(EventType.CREATOR_SECOND_REVIEW_DEADLINE)
                    .targetId(campaign.getId())
                    .targetType(TargetType.CAMPAIGN)
                    .executeAt(campaign.getReviewSubmissionDeadline())
                    .status(EventStatus.PENDING)
                    .build());

            // 캠페인 완료 처리
            events.add(ScheduledEvent.builder()
                    .eventType(EventType.CAMPAIGN_COMPLETE)
                    .targetId(campaign.getId())
                    .targetType(TargetType.CAMPAIGN)
                    .executeAt(campaign.getReviewSubmissionDeadline())
                    .status(EventStatus.PENDING)
                    .build());
        }

        // 이벤트 저장
        scheduledEventRepository.saveAll(events);
    }
}