package com.lokoko.domain.campaignReview.api.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FirstReviewUploadRequest(

        /**
         * 첫번째 리뷰 업로드시 입력받을 필드들
         */

        @Schema(requiredMode = REQUIRED, description = "콘텐츠 포맷", example = "INSTA_REELS")
        @NotNull
        ContentType contentType,

        @Schema(description = "리뷰 미디어 URL 리스트(이미지 또는 영상)", example = "[\"https://s3.example.com/review/2025/09/.../img1.jpg\"]")
        @NotEmpty
        List<String> mediaUrls,

        @Schema(description = "캡션 + 해시태그 (최대 2200자)", example = "Hydrating mask review 💧 #hydration #mask #skincare")
        @NotBlank
        @Size(max = 2200)
        String captionWithHashtags
) {
}
