package com.lokoko.domain.scheduler.application.service;

import com.lokoko.domain.scheduler.domain.entity.ScheduledEvent;
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
    private final EventProcessorService eventProcessorService;

    /**
     * 1분마다 실행되는 스케줄러
     * 실행 시간이 된 이벤트들을 찾아서 처리
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 0) // 이전 실행 완료 후 60초 대기
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
                eventProcessorService.processEvent(event);
            } catch (Exception e) {
                log.error("Failed to process event {}: {}", event.getId(), e.getMessage(), e);
            }
        }
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