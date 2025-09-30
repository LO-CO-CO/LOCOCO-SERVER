package com.lokoko.domain.scheduler.domain.repository;

import com.lokoko.domain.scheduler.domain.entity.ScheduledEvent;
import com.lokoko.domain.scheduler.domain.enums.EventStatus;
import com.lokoko.domain.scheduler.domain.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ScheduledEventRepository extends JpaRepository<ScheduledEvent, Long> {

    /**
     * 실행해야 할 대기중인 이벤트 조회
     * @param now 현재 시간
     * @return 실행 대상 이벤트 목록
     */
    @Query("SELECT e FROM ScheduledEvent e WHERE e.executeAt <= :now AND e.status = :status ORDER BY e.executeAt")
    List<ScheduledEvent> findPendingEventsBefore(@Param("now") Instant now, @Param("status") EventStatus status);

    default List<ScheduledEvent> findPendingEventsBefore(Instant now) {
        return findPendingEventsBefore(now, EventStatus.PENDING);
    }

    /**
     * 특정 대상의 대기중인 이벤트 조회
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 대기중인 이벤트 목록
     */
    @Query("SELECT e FROM ScheduledEvent e WHERE e.targetType = :targetType AND e.targetId = :targetId AND e.status = :status")
    List<ScheduledEvent> findPendingEventsByTarget(
            @Param("targetType") TargetType targetType,
            @Param("targetId") Long targetId,
            @Param("status") EventStatus status
    );

    default List<ScheduledEvent> findPendingEventsByTarget(TargetType targetType, Long targetId) {
        return findPendingEventsByTarget(targetType, targetId, EventStatus.PENDING);
    }

    /**
     * 특정 대상의 대기중인 이벤트 삭제
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     */
    @Modifying
    @Query("DELETE FROM ScheduledEvent e WHERE e.targetType = :targetType AND e.targetId = :targetId AND e.status = 'PENDING'")
    void deletePendingEventsByTarget(
            @Param("targetType") TargetType targetType,
            @Param("targetId") Long targetId
    );

    /**
     * 실패한 이벤트 중 재시도 가능한 이벤트 조회
     * @param maxRetryCount 최대 재시도 횟수
     * @return 재시도 가능한 이벤트 목록
     */
    @Query("SELECT e FROM ScheduledEvent e WHERE e.status = 'FAILED' AND e.retryCount < :maxRetryCount")
    List<ScheduledEvent> findRetryableEvents(@Param("maxRetryCount") Integer maxRetryCount);

    /**
     * 오래된 실행 완료 이벤트 삭제
     * @param beforeDate 이 날짜 이전에 실행된 이벤트 삭제
     */
    @Modifying
    @Query("DELETE FROM ScheduledEvent e WHERE e.status = 'EXECUTED' AND e.executedAt < :beforeDate")
    void deleteOldExecutedEvents(@Param("beforeDate") Instant beforeDate);

    /**
     * 특정 캠페인의 모든 이벤트 조회
     * @param campaignId 캠페인 ID
     * @return 캠페인 관련 모든 이벤트
     */
    @Query("SELECT e FROM ScheduledEvent e WHERE e.targetType = 'CAMPAIGN' AND e.targetId = :campaignId ORDER BY e.executeAt")
    List<ScheduledEvent> findByCampaignId(@Param("campaignId") Long campaignId);
}