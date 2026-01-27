package com.lokoko.domain.productReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ImageReviewProductDetailResponse(
        @Schema(requiredMode = REQUIRED)
        Long reviewId,
        @Schema(requiredMode = REQUIRED)
        LocalDateTime writtenTime,
        @Schema(requiredMode = REQUIRED)
        String positiveComment,
        @Schema(requiredMode = REQUIRED)
        String negativeComment,
        @Schema(requiredMode = REQUIRED)
        String profileImageUrl,
        @Schema(requiredMode = REQUIRED)
        String authorName,
        @Schema(requiredMode = REQUIRED)
        Long authorId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0")
        @Schema(requiredMode = REQUIRED)
        Double rating,
        @Schema(requiredMode = REQUIRED)
        Integer likeCount,
        @Schema(requiredMode = REQUIRED)
        List<String> images,
        @Schema(requiredMode = REQUIRED)
        Boolean isLiked,
        @Schema(requiredMode = REQUIRED)
        Boolean isMine
) {

    public ImageReviewProductDetailResponse(Long reviewId, LocalDateTime writtenTime,
                                             String positiveComment,
                                            String negativeComment, String profileImageUrl, String authorName,
                                            Long authorId, Double rating,
                                            Integer likeCount, List<String> images,
                                            Boolean isLiked, Boolean isMine
    ) {
        this.reviewId = reviewId;
        this.writtenTime = writtenTime;
        this.positiveComment = positiveComment;
        this.negativeComment = negativeComment;
        this.profileImageUrl = profileImageUrl;
        this.authorName = authorName;
        this.authorId = authorId;
        this.rating = rating;
        this.likeCount = likeCount;
        this.images = images != null ? new ArrayList<>(images) : new ArrayList<>();
        this.isLiked = isLiked;
        this.isMine = isMine;
    }
}