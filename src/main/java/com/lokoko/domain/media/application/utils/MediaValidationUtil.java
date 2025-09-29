package com.lokoko.domain.media.application.utils;

import com.lokoko.domain.productReview.exception.ErrorMessage;
import com.lokoko.domain.productReview.exception.InvalidMediaTypeException;
import java.util.List;

public class MediaValidationUtil {

    private static final int MAX_TOTAL_MEDIA = 15;

    public static void validateMediaFiles(List<String> mediaUrls) {
        if (mediaUrls != null && !mediaUrls.isEmpty()) {
            long videoCount = mediaUrls.stream().filter(url -> url.contains("/video/")).count();
            long imageCount = mediaUrls.stream().filter(url -> url.contains("/image/")).count();

            if (videoCount > 0 && imageCount > 0) {
                throw new InvalidMediaTypeException(ErrorMessage.MIXED_MEDIA_TYPE_NOT_ALLOWED);
            }
            if (videoCount > 1) {
                throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_VIDEO_FILES);
            }
            if (imageCount > 5) {
                throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_IMAGE_FILES);
            }
        }
    }

    public static void validateTotalMediaCount(List<String> mediaUrls) {
        if (mediaUrls != null && mediaUrls.size() > MAX_TOTAL_MEDIA) {

            throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_MEDIA_FILES);
        }
    }

    public static void validateMediaCounts(List<String> mediaUrls) {
        MediaValidationUtil.validateTotalMediaCount(mediaUrls);
    }

    public static void validateTotalMediaCount(List<String> imageUrls, List<String> videoUrls) {
        int imageCnt = (imageUrls == null) ? 0 : imageUrls.size();
        int videoCnt = (videoUrls == null) ? 0 : videoUrls.size();
        if (imageCnt + videoCnt > MAX_TOTAL_MEDIA) {
            throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_MEDIA_FILES);
        }
    }
}
