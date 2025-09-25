package com.lokoko.domain.campaignReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignReviewDetailResponse(

        @Schema(requiredMode = REQUIRED, description = "캠페인 ID", example = "11")
        Long campaignId,

        @Schema(requiredMode = REQUIRED, description = "캠페인 제목", example = "Summer Hydration Campaign")
        String title,

        @Schema(requiredMode = REQUIRED, description = "소셜 클립 컨텐츠 종류", example = "TIKTOK_VIDEO")
        ContentType contentType,

        @Schema(requiredMode = REQUIRED, description = "리뷰 미디어 URL 리스트(이미지 또는 영상)", example = "[\"https://s3.amazonaws.com/bucket/image...\", \"https://s3.amazonaws.com/bucket/video...\"]")
        @NotEmpty
        List<String> mediaUrls,

        @Schema(requiredMode = REQUIRED, description = "캡션(해시태그 포함)", example = "Enjoying the summer vibes! #SummerHydration #StayCool")
        String captionWithHashtags,

        @Schema(description = "게시물 URL (1차 리뷰 반환시에는 비어있음)", example = "https://www.instagram.com/p/ExamplePost/")
        String postUrl
) {
}