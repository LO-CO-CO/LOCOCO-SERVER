package com.lokoko.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ImageReviewProductDetailResponse(
        Long reviewId,
        LocalDateTime writtenTime,
        Boolean receiptUploaded,
        String positiveComment,
        String negativeComment,
        String authorName,
        Double rating,
        String option,
        int likeCount,
        List<String> images
) {

    public ImageReviewProductDetailResponse(Long reviewId, LocalDateTime writtenTime,
                                            Boolean receiptUploaded, String positiveComment,
                                            String negativeComment, String authorName,
                                            Double rating, String option,
                                            int likeCount, List<String> images) {
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
