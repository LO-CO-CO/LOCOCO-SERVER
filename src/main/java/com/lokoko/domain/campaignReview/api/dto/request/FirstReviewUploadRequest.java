package com.lokoko.domain.campaignReview.api.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.socialclip.domain.entity.enums.Content;
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

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "콘텐츠 포맷", example = "INSTA_REELS")
        @NotNull
        Content content,

        @Schema(description = "캠페인 리뷰 이미지 URL 리스트 (최소 1장)", example = "[\"https://s3.example.com/review/img1.jpg\", \"https://s3.example.com/review/img2.jpg\"]")
        @NotEmpty
        @Size(min = 1)
        List<String> imageUrls,

        @Schema(description = "캡션 + 해시태그 (최대 2200자)", example = "Hydrating mask review 💧 #hydration #mask #skincare")
        @NotBlank
        @Size(max = 2200)
        String captionWithHashtags
) {
}
