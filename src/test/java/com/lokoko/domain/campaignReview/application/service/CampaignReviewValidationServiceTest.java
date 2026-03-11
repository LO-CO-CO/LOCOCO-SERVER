package com.lokoko.domain.campaignReview.application.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lokoko.domain.campaignReview.exception.ErrorMessage;
import com.lokoko.domain.campaignReview.exception.InvalidReviewPayloadException;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.domain.productReview.exception.InvalidMediaTypeException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("캠페인 리뷰 업로드 검증")
class CampaignReviewValidationServiceTest {

    private final CampaignReviewValidationService campaignReviewValidationService =
            new CampaignReviewValidationService();

    @Test
    @DisplayName("validateTwoSetCombination() : 첫 번째 플랫폼이 없으면 예외를 던진다")
    void validateTwoSetCombination_requiresFirstPlatform() {
        assertThatThrownBy(() -> campaignReviewValidationService.validateTwoSetCombination(
                null, ContentType.TIKTOK_VIDEO))
                .isInstanceOf(InvalidReviewPayloadException.class)
                .hasMessage(ErrorMessage.MISSING_PLATFORM.getMessage());
    }

    @Test
    @DisplayName("requireFirstSetPresent() : 1차 리뷰 입력이 불완전하면 예외를 던진다")
    void requireFirstSetPresent_rejectsIncompleteContent() {
        assertThatThrownBy(() -> campaignReviewValidationService.requireFirstSetPresent(List.of("media"), " "))
                .isInstanceOf(InvalidReviewPayloadException.class)
                .hasMessage(ErrorMessage.FIRST_SET_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("ensureSecondSetAbsentForSecondRound() : 허용되지 않은 두 번째 세트가 있으면 예외를 던진다")
    void ensureSecondSetAbsentForSecondRound_rejectsUnexpectedSecondSet() {
        assertThatThrownBy(() -> campaignReviewValidationService.ensureSecondSetAbsentForSecondRound(
                List.of("media"), null, null))
                .isInstanceOf(InvalidReviewPayloadException.class)
                .hasMessage(ErrorMessage.SECOND_SET_NOT_ALLOWED.getMessage());
    }

    @Test
    @DisplayName("requireSecondSetPresent() : media 와 caption 이 있으면 postUrl 도 필수다")
    void requireSecondSetPresent_requiresPostUrl() {
        assertThatThrownBy(() -> campaignReviewValidationService.requireSecondSetPresent(
                List.of("media"), "caption", " "))
                .isInstanceOf(InvalidReviewPayloadException.class)
                .hasMessage(ErrorMessage.SECOND_POST_URL_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("validateCombinedMediaLimit() : 두 세트 합산 media 가 15개를 넘으면 예외를 던진다")
    void validateCombinedMediaLimit_rejectsMoreThanFifteenUrls() {
        List<String> first = List.of(
                "1", "2", "3", "4", "5", "6", "7", "8"
        );
        List<String> second = List.of(
                "9", "10", "11", "12", "13", "14", "15", "16"
        );

        assertThatThrownBy(() -> campaignReviewValidationService.validateCombinedMediaLimit(first, second))
                .isInstanceOf(InvalidMediaTypeException.class);
    }

    @Test
    @DisplayName("단일 플랫폼 1차 업로드에서는 두 번째 세트가 없어도 통과한다")
    void ensureSecondSetAbsentForFirstRound_allowsMissingSecondSet() {
        assertThatCode(() -> campaignReviewValidationService.ensureSecondSetAbsentForFirstRound(null, null))
                .doesNotThrowAnyException();
    }
}
