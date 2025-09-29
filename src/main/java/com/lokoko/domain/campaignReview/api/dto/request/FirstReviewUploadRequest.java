package com.lokoko.domain.campaignReview.api.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FirstReviewUploadRequest(

        /**
         * 첫번째 리뷰 업로드시 입력받을 필드들
         */

        @Schema(description = "첫번째 1차 미디어 URL 리스트(이미지 또는 영상)", example = "[\"https://s3.example.com/review/2025/09/.../img1.jpg\"]")
        @NotEmpty
        List<String> firstMediaUrls,

        @Schema(description = "첫번째 캡션 + 해시태그 (최대 2200자)", example = "Hydrating mask review 💧 #hydration #mask #skincare")
        @NotBlank
        @Size(max = 2200)
        String firstCaptionWithHashtags,

        /**
         * 두번째 필드(선택) — secondContentType이 있는 캠페인이면 입력받아야 함
         */

        @Schema(description = "두번째 1차 미디어 URL 리스트(선택)", example = "[\"https://s3.example.com/review/2025/09/.../img1.jpg\"]")
        List<String> secondMediaUrls,

        @Schema(description = "두번째 1차 캡션+해시태그(선택)", example = "Hydrating mask review 💧 #hydration #mask #skincare")
        @Size(max = 2200)
        String secondCaptionWithHashtags
) {
}
