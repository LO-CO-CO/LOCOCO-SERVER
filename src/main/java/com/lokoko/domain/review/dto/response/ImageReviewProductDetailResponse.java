package com.lokoko.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ImageReviewProductDetailResponse(
        @Schema(requiredMode = REQUIRED)
        Long reviewId,
        @Schema(requiredMode = REQUIRED)
        LocalDateTime writtenTime,
        @Schema(requiredMode = REQUIRED)
        Boolean receiptUploaded,
        @Schema(requiredMode = REQUIRED)
        String positiveComment,
        @Schema(requiredMode = REQUIRED)
        String negativeComment,
        @Schema(requiredMode = REQUIRED)
        String authorName,
        @Schema(requiredMode = REQUIRED)
        Double rating,
        @Schema(requiredMode = REQUIRED)
        String option,
        @Schema(requiredMode = REQUIRED)
        Integer likeCount,
        @Schema(requiredMode = REQUIRED)
        List<String> images
) {

    public ImageReviewProductDetailResponse(Long reviewId, LocalDateTime writtenTime,
                                            Boolean receiptUploaded, String positiveComment,
                                            String negativeComment, String authorName,
                                            Double rating, String option,
                                            Integer likeCount, List<String> images) {
        this.reviewId = reviewId;
        this.writtenTime = writtenTime;
        this.receiptUploaded = receiptUploaded;
        this.positiveComment = positiveComment;
        this.negativeComment = negativeComment;
        this.authorName = authorName;
        this.rating = rating;
        this.option = option;
        this.likeCount = likeCount;
        this.images = new ArrayList<>(images);
    }


}
