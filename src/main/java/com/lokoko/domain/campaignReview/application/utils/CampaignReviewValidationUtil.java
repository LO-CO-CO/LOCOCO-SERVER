package com.lokoko.domain.campaignReview.application.utils;

import com.lokoko.domain.campaignReview.exception.ErrorMessage;
import com.lokoko.domain.campaignReview.exception.InvalidReviewPayloadException;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import java.util.List;

public final class CampaignReviewValidationUtil {

    private CampaignReviewValidationUtil() {
    }

    /**
     * 캠페인에 설정된 리뷰의 두 컨텐츠 타입이 올바른지 검증
     */
    public static void validateTwoSetCombination(ContentType a, ContentType b) {
        if (b == null) {
            return;
        }
        if (a == null) {
            throw new InvalidReviewPayloadException(ErrorMessage.MISSING_PLATFORM);
        }
    }

    /**
     * 1차/2차 공통: 리뷰 입력 묶음(미디어+캡션)이 모두 존재하는지 검증
     */
    public static void requireSetPresent(List<String> media, String caption, boolean firstSet) {
        if (media == null || media.isEmpty() || caption == null || caption.isBlank()) {
            throw new InvalidReviewPayloadException(
                    firstSet ? ErrorMessage.FIRST_SET_REQUIRED : ErrorMessage.SECOND_SET_REQUIRED
            );
        }
    }

    /**
     * 단일 리뷰 캠페인: 2번째 리뷰 입력(미디어/캡션)이 오면 안 됨
     */
    public static void ensureSecondSetAbsentForFirstRound(List<String> media, String caption) {
        if ((media != null && !media.isEmpty()) || (caption != null && !caption.isBlank())) {
            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_SET_NOT_ALLOWED);
        }
    }

    /**
     * 단일 리뷰 캠페인: 2번째 리뷰 입력(미디어/캡션/postUrl)이 오면 안 됨
     */
    public static void ensureSecondSetAbsentForSecondRound(List<String> media, String caption, String postUrl) {
        if ((media != null && !media.isEmpty())
                || (caption != null && !caption.isBlank())
                || (postUrl != null && !postUrl.isBlank())) {
            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_SET_NOT_ALLOWED);
        }
    }
}
