package com.lokoko.domain.scheduler.application.service;

import com.lokoko.domain.scheduler.domain.entity.ScheduledEvent;
import com.lokoko.domain.scheduler.domain.enums.EventStatus;
import com.lokoko.domain.scheduler.domain.repository.ScheduledEventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
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
     * 동시성 제어를 위해 PENDING -> PROCESSING 원자적 업데이트 후 실행
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processEvent(ScheduledEvent event) {
        try {
            // 1. PENDING -> PROCESSING 원자적 상태 변경 (동시성 제어)
            int updated = scheduledEventRepository.updateStatusToProcessing(event.getId());

            if (updated == 0) {
                // 이미 다른 스레드에서 처리중이거나 처리됨
                return;
            }

            // 2. 최신 상태로 다시 조회 (낙관적 락 버전 확인용)
            ScheduledEvent processingEvent = scheduledEventRepository.findById(event.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Event not found: " + event.getId()));

            // 3. 이벤트 실행
            eventExecutionService.execute(processingEvent);

            // 4. 성공 처리
            processingEvent.markAsExecuted();
            scheduledEventRepository.save(processingEvent);

        } catch (OptimisticLockingFailureException e) {
            // 낙관적 락 충돌 - 다른 스레드에서 이미 처리중

        } catch (EntityNotFoundException e) {
            // 복구 불가능한 에러 - 재시도 안 함
            log.error("Event target not found: {} for target: {} - will not retry",
                    event.getEventType(), event.getTargetId());
            // 별도 트랜잭션에서 실패 처리
            markEventAsFailed(event.getId(), e.getMessage(), false);

        } catch (Exception e) {
            // 일시적 에러 - 재시도 가능
            log.error("Failed to execute event: {} for target: {} - Error: {}",
                    event.getEventType(), event.getTargetId(), e.getMessage());

            // 별도 트랜잭션에서 실패 처리 및 재시도
            markEventAsFailed(event.getId(), e.getMessage(), true);
        }
    }

    /**
     * 이벤트 실패 처리 (재시도 여부 제어)
     * 별도의 트랜잭션으로 실행하여 실패 정보가 확실히 저장되도록 함
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markEventAsFailed(Long eventId, String errorMessage, boolean allowRetry) {
        scheduledEventRepository.findById(eventId).ifPresent(event -> {
            event.markAsFailed(errorMessage);
            scheduledEventRepository.save(event);

            if (allowRetry && event.isRetryable()) {
                scheduleRetry(event);
            }
        });
    }

    /**
     * 실패한 이벤트 재시도 스케줄링 (지수 백오프)
     */
    private void scheduleRetry(ScheduledEvent failedEvent) {
        // 지수 백오프: 5분 -> 10분 -> 20분
        long delayMinutes = 5L * (long) Math.pow(2, failedEvent.getRetryCount() - 1);
        Instant retryAt = Instant.now().plus(Duration.ofMinutes(delayMinutes));

        ScheduledEvent retryEvent = ScheduledEvent.builder()
                .eventType(failedEvent.getEventType())
                .targetId(failedEvent.getTargetId())
                .targetType(failedEvent.getTargetType())
                .executeAt(retryAt)
                .retryCount(failedEvent.getRetryCount())
                .status(EventStatus.PENDING)
                .build();

        scheduledEventRepository.save(retryEvent);

        log.info("Scheduled retry for event {} (attempt {}) at {}",
                failedEvent.getId(), failedEvent.getRetryCount(), retryAt);
    }
}