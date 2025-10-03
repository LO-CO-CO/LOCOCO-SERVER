package com.lokoko.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 스케줄링 설정
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

    /**
     * 스케줄러 스레드 풀 설정
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // 스레드 풀 크기
        scheduler.setThreadNamePrefix("scheduler-"); // 스레드 이름 prefix
        scheduler.setAwaitTerminationSeconds(60); // 종료 대기 시간
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // 종료 시 작업 완료 대기
        scheduler.initialize();
        return scheduler;
    }
}