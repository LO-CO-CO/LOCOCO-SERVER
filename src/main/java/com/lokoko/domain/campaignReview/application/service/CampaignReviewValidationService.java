package com.lokoko.domain.campaignReview.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lokoko.domain.campaignReview.exception.ErrorMessage;
import com.lokoko.domain.campaignReview.exception.InvalidReviewPayloadException;
import com.lokoko.domain.media.application.utils.MediaValidationUtil;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;

@Service
public class CampaignReviewValidationService {

    public void validateTwoSetCombination(ContentType first, ContentType second) {
        if (first == null) {
            throw new InvalidReviewPayloadException(ErrorMessage.MISSING_PLATFORM);
        }
        if (second == null) {
            return;
        }
    }

    public void requireFirstSetPresent(List<String> media, String caption) {
        if (media == null || media.isEmpty() || caption == null || caption.isBlank()) {
            throw new InvalidReviewPayloadException(ErrorMessage.FIRST_SET_REQUIRED);
        }
    }

    public void ensureSecondSetAbsentForFirstRound(List<String> media, String caption) {
        if ((media != null && !media.isEmpty()) || (caption != null && !caption.isBlank())) {
            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_SET_NOT_ALLOWED);
        }
    }

    public void requireSecondSetPresent(List<String> media, String caption, String postUrl) {
        if (media == null || media.isEmpty() || caption == null || caption.isBlank()) {
            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_SET_REQUIRED);
        }
        if (postUrl == null || postUrl.isBlank()) {
            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_POST_URL_REQUIRED);
        }
    }

    public void ensureSecondSetAbsentForSecondRound(List<String> media, String caption, String postUrl) {
        if ((media != null && !media.isEmpty())
                || (caption != null && !caption.isBlank())
                || (postUrl != null && !postUrl.isBlank())) {
            throw new InvalidReviewPayloadException(ErrorMessage.SECOND_SET_NOT_ALLOWED);
        }
    }

    public void validateCombinedMediaLimit(List<String> firstMediaUrls, List<String> secondMediaUrls) {
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
