package com.lokoko.global.config;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
/**
 * 베타 기능 설정을 관리하는 Configuration 클래스
 */
@Configuration
@Getter
public class BetaFeatureConfig {

    /**
     * 베타 버전(서울 1988 캠페인 진행)에서는 , true 설정
     * 정식 버전에서는 , false 설정
     * 리뷰 업로드 플로우를 간소화할지 말지 결정
     * 기본값: false (정식 플로우)
     */
    private boolean simplifiedReviewFlow = true;

    /**
     * 베타버전((서울 1988 캠페인 진행)에서는, true 설정
     * 정식 버전에서는 , false 설정
     * 1차 리뷰 업로드시, post URL 을 포함할지 말지 여부를 결정
     */
    private boolean firstReviewUrlEnabled = true;
}