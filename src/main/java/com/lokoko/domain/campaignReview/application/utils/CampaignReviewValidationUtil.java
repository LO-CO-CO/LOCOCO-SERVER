package com.lokoko.domain.campaignReview.application.utils;

import com.lokoko.domain.campaignReview.exception.ErrorMessage;
import com.lokoko.domain.campaignReview.exception.InvalidReviewPayloadException;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import java.util.List;

public final class CampaignReviewValidationUtil {

    private CampaignReviewValidationUtil() {
    }

    /**
     * 캠페인에 설정된 두 컨텐츠 타입 조합 기본 검증 - second가 null이면 단일 타입 캠페인 - second가 있으면 first는 반드시 존재해야 함
     */
    public static void validateTwoSetCombination(ContentType first, ContentType second) {
        if (second == null) {
            return;
        }
        if (first == null) {

            throw new InvalidReviewPayloadException(ErrorMessage.MISSING_PLATFORM);
        }
    }

    /**
     * 1차용: 미디어 + 캡션 필수( postUrl 없음 )
     */
    public static void requireFirstSetPresent(List<String> media, String caption) {
        if (media == null || media.isEmpty() || caption == null || caption.isBlank()) {

            throw new InvalidReviewPayloadException(ErrorMessage.FIRST_SET_REQUIRED);
        }
    }

    /**
     * 2차용: 미디어 + 캡션 + postUrl 모두 필수
     */
    public static void requireSecondSetPresent(List<String> media, String caption, String postUrl, boolean firstType) {
        if (media == null || media.isEmpty() || caption == null || caption.isBlank()) {

            throw new InvalidReviewPayloadException(
                    firstType ? ErrorMessage.FIRST_SET_REQUIRED : ErrorMessage.SECOND_SET_REQUIRED
            );
        }
        if (postUrl == null || postUrl.isBlank()) {

            throw new InvalidReviewPayloadException(
                    firstType ? ErrorMessage.FIRST_POST_URL_REQUIRED : ErrorMessage.SECOND_POST_URL_REQUIRED
            );
        }
    }

    /**
     * 단일 타입 캠페인(1차): 두 번째 입력이 오면 안 됨
     */
    public static void ensureSecondSetAbsentForFirstRound(List<String> media, String caption) {
        if ((media != null && !media.isEmpty()) || (caption != null && !caption.isBlank())) {

            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_SET_NOT_ALLOWED);
        }
    }

    /**
     * 단일 타입 캠페인(2차): 두 번째 입력이 오면 안 됨 (postUrl 포함)
     */
    public static void ensureSecondSetAbsentForSecondRound(List<String> media, String caption, String postUrl) {
        if ((media != null && !media.isEmpty())
                || (caption != null && !caption.isBlank())
                || (postUrl != null && !postUrl.isBlank())) {

            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_SET_NOT_ALLOWED);
        }
    }
}
