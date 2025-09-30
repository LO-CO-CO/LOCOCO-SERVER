package com.lokoko.domain.scheduler.application.service;

import com.lokoko.domain.scheduler.domain.entity.ScheduledEvent;
import com.lokoko.domain.scheduler.domain.enums.EventStatus;
import com.lokoko.domain.scheduler.domain.repository.ScheduledEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * 스케줄된 이벤트를 처리하는 메인 스케줄러
 * 1분마다 실행되어 대기중인 이벤트를 확인하고 실행
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventProcessor {

    private final ScheduledEventRepository scheduledEventRepository;
    private final EventExecutionService eventExecutionService;

    /**
     * 1분마다 실행되는 스케줄러
     * 실행 시간이 된 이벤트들을 찾아서 처리
     */
    @Scheduled(fixedRate = 60000) // 1분(60초 = 60000ms)마다 실행
    public void processScheduledEvents() {
        Instant now = Instant.now();

        // 실행해야 할 이벤트들 조회
        List<ScheduledEvent> eventsToExecute = scheduledEventRepository
                .findPendingEventsBefore(now);

        if (eventsToExecute.isEmpty()) {
            return;
        }

        log.info("Processing {} scheduled events at {}", eventsToExecute.size(), now);

        // 각 이벤트를 개별적으로 처리 (하나의 실패가 다른 이벤트 처리를 막지 않도록)
        for (ScheduledEvent event : eventsToExecute) {
            try {
                processEvent(event);
            } catch (Exception e) {
                log.error("Failed to process event {}: {}", event.getId(), e.getMessage(), e);
            }
        }
    }

    /**
     * 개별 이벤트 처리
     */
    @Transactional
    protected void processEvent(ScheduledEvent event) {
        log.debug("Processing event: {} for target: {} ({})",
                event.getEventType(), event.getTargetId(), event.getTargetType());

        try {
            // 이벤트 실행
            eventExecutionService.execute(event);

            // 성공 처리
            event.markAsExecuted();
            scheduledEventRepository.save(event);

            log.info("Successfully executed event: {} for target: {} ({})",
                    event.getEventType(), event.getTargetId(), event.getTargetType());

        } catch (Exception e) {
            // 실패 처리
            event.markAsFailed(e.getMessage());
            scheduledEventRepository.save(event);

            log.error("Failed to execute event: {} for target: {} - Error: {}",
                    event.getEventType(), event.getTargetId(), e.getMessage());

            // 재시도 가능한 경우 재시도 이벤트 생성
            if (event.isRetryable()) {
                scheduleRetry(event);
            }
        }
    }

    /**
     * 실패한 이벤트 재시도 스케줄링
     */
    private void scheduleRetry(ScheduledEvent failedEvent) {
        // 5분 후 재시도
        Instant retryAt = Instant.now().plus(Duration.ofMinutes(5));

        ScheduledEvent retryEvent = ScheduledEvent.builder()
                .eventType(failedEvent.getEventType())
                .targetId(failedEvent.getTargetId())
                .targetType(failedEvent.getTargetType())
                .executeAt(retryAt)
                .retryCount(failedEvent.getRetryCount())
                .status(EventStatus.PENDING)
                .build();

        scheduledEventRepository.save(retryEvent);
    }

    /**
     * 매일 자정에 실행되는 정리 작업
     * 30일 이상 지난 실행 완료 이벤트를 삭제
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    @Transactional
    public void cleanupOldEvents() {
        Instant thirtyDaysAgo = Instant.now().minus(Duration.ofDays(30));

        scheduledEventRepository.deleteOldExecutedEvents(thirtyDaysAgo);

        log.info("Cleaned up executed events older than {}", thirtyDaysAgo);
    }
}