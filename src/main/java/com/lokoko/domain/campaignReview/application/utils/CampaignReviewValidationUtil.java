package com.lokoko.domain.campaignReview.application.utils;

import com.lokoko.domain.campaignReview.exception.ErrorMessage;
import com.lokoko.domain.campaignReview.exception.InvalidReviewPayloadException;
import com.lokoko.domain.media.application.utils.MediaValidationUtil;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import java.util.ArrayList;
import java.util.List;

/**
 * 리뷰 업로드 요청에 대한 공통 유효성 검증 유틸.
 * <p>
 * - 캠페인에 설정된 컨텐츠 플랫폼 조합 검증(첫 플랫폼 필수, 두 번째는 선택)<br> - 1차/2차 업로드 시 세트(미디어/캡션[/포스트URL]) 필수/금지 검증<br> - 세트별/합산 미디어 개수 제한
 * 검증(합산=최대 15개 등은 MediaValidationUtil에 위임)
 */
public final class CampaignReviewValidationUtil {

    private CampaignReviewValidationUtil() {
    }

    /**
     * 캠페인에 설정된 리뷰 컨텐츠 타입 조합 검증.
     * <ul>
     *   <li>first(첫 플랫폼)는 항상 필수, 누락 시 {@link ErrorMessage#MISSING_PLATFORM}</li>
     *   <li>second는 선택(없으면 단일 타입 캠페인으로 간주)</li>
     *   <li>중복/조합 제한이 필요하면 여기서 추가(현 요구사항상 제한 없음)</li>
     * </ul>
     */
    public static void validateTwoSetCombination(ContentType first, ContentType second) {
        // 첫 플랫폼은 필수
        if (first == null) {
            throw new InvalidReviewPayloadException(ErrorMessage.MISSING_PLATFORM);
        }
        // 두 번째가 없으면 단일 타입 캠페인 → OK
        if (second == null) {
            return;
        }
    }

    /**
     * 1차 업로드용: 한 리뷰 입력(미디어+캡션)이 모두 존재해야 함. 누락 시 {@link ErrorMessage#FIRST_SET_REQUIRED}.
     */
    public static void requireFirstSetPresent(List<String> media, String caption) {
        if (media == null || media.isEmpty() || caption == null || caption.isBlank()) {
            throw new InvalidReviewPayloadException(ErrorMessage.FIRST_SET_REQUIRED);
        }
    }

    /**
     * 단일 타입 캠페인에서 1차 업로드 시, 두 번째 리뷰 입력이 오면 안 됨. 전달되면 {@link ErrorMessage#SECOND_SET_NOT_ALLOWED}.
     */
    public static void ensureSecondSetAbsentForFirstRound(List<String> media, String caption) {
        if ((media != null && !media.isEmpty()) || (caption != null && !caption.isBlank())) {
            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_SET_NOT_ALLOWED);
        }
    }

    /**
     * 2차 업로드용: 한 리뷰 입력(미디어+캡션+postUrl)이 모두 존재해야 함. 누락 시 컨텐츠/URL 각각 {@link ErrorMessage#SECOND_SET_REQUIRED} 또는
     * {@link ErrorMessage#SECOND_POST_URL_REQUIRED}.
     *
     * @param _unused 기존 호출 시그니처(불린 4번째 인자) 호환을 위해 유지, 사용하지 않음
     */
    public static void requireSecondSetPresent(List<String> media, String caption, String postUrl, boolean _unused) {
        requireSecondSetPresent(media, caption, postUrl);
    }

    /**
     * 2차 업로드용: 한 리뷰 입력(미디어+캡션+postUrl)이 모두 존재해야 함. 누락 시 컨텐츠/URL 각각 예외 발생
     */
    public static void requireSecondSetPresent(List<String> media, String caption, String postUrl) {
        // 컨텐츠(미디어+캡션) 누락
        if (media == null || media.isEmpty() || caption == null || caption.isBlank()) {
            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_SET_REQUIRED);
        }
        // URL 누락
        if (postUrl == null || postUrl.isBlank()) {
            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_POST_URL_REQUIRED);
        }
    }

    /**
     * 단일 타입 캠페인에서 2차 업로드시, 두 번째 리뷰 입력(미디어/캡션/URL)이 오면 안됨. 전달되면 예외 발생
     */
    public static void ensureSecondSetAbsentForSecondRound(List<String> media, String caption, String postUrl) {
        if ((media != null && !media.isEmpty())
                || (caption != null && !caption.isBlank())
                || (postUrl != null && !postUrl.isBlank())) {
            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_SET_NOT_ALLOWED);
        }
    }

    /**
     * 두 리뷰 입력(첫/두 번째)의 미디어 URL을 합산하여 전체 개수 제한을 검증. 개수 초과시 예외 발생
     */
    public static void validateCombinedMediaLimit(List<String> firstMediaUrls, List<String> secondMediaUrls) {
        List<String> combined = new ArrayList<>();
        if (firstMediaUrls != null) {
            combined.addAll(firstMediaUrls);
        }
        if (secondMediaUrls != null) {
            combined.addAll(secondMediaUrls);
        }
        MediaValidationUtil.validateTotalMediaCount(combined);
    }
}
