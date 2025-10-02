package com.lokoko.domain.scheduler.domain.entity;

import com.lokoko.domain.scheduler.domain.enums.EventStatus;
import com.lokoko.domain.scheduler.domain.enums.EventType;
import com.lokoko.domain.scheduler.domain.enums.TargetType;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "scheduled_events",
    indexes = {
        @Index(name = "idx_execute_at_status", columnList = "execute_at, status"),
        @Index(name = "idx_target_type_id", columnList = "target_type, target_id")
    }
)
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ScheduledEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventType eventType;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TargetType targetType;

    @Column(nullable = false)
    private Instant executeAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EventStatus status = EventStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    private Instant executedAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 이벤트 실행 완료 처리
     */
    public void markAsExecuted() {
        this.status = EventStatus.EXECUTED;
        this.executedAt = Instant.now();
    }

    /**
     * 이벤트 실패 처리
     */
    public void markAsFailed(String error) {
        this.status = EventStatus.FAILED;
        this.errorMessage = error;
        this.retryCount++;
    }

    /**
     * 재시도 가능 여부 확인
     */
    public boolean isRetryable() {
        return this.retryCount < 3 && this.status == EventStatus.FAILED;
    }
}