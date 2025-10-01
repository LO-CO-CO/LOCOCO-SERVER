package com.lokoko.domain.scheduler.application.service;

import com.lokoko.domain.scheduler.domain.entity.ScheduledEvent;
import com.lokoko.domain.scheduler.domain.enums.EventStatus;
import com.lokoko.domain.scheduler.domain.repository.ScheduledEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProcessorService {

    private final ScheduledEventRepository scheduledEventRepository;
    private final EventExecutionService eventExecutionService;

    /**
     * 개별 이벤트 처리
     * 각 이벤트는 독립적인 트랜잭션에서 실행
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processEvent(ScheduledEvent event) {
        try {
            // 이벤트 실행
            eventExecutionService.execute(event);

            // 성공 처리
            event.markAsExecuted();
            scheduledEventRepository.save(event);

            log.debug("Successfully executed event: {} for target: {} ({})",
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
}