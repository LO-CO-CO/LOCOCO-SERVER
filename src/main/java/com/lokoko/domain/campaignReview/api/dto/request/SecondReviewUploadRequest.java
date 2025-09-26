package com.lokoko.domain.campaignReview.api.dto.request;


import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SecondReviewUploadRequest(

        /**
         * 두번째 리뷰 업로드시 입력받을 필드들 (명시적으로 다 입력 받기)
         */

        @Schema(requiredMode = REQUIRED, description = "첫번째 2차 미디어 URL 리스트")
        @NotEmpty
        List<String> firstMediaUrls,

        @Schema(description = "첫번째 2차 캡션+해시태그 (최대 2200자)", example = "Hydrating mask review 💧 #hydration #mask #skincare")
        @NotBlank
        @Size(max = 2200)
        String firstCaptionWithHashtags,

        @Schema(requiredMode = REQUIRED, description = "첫번째 2차 게시물 URL", example = "https://www.instagram.com/p/XXXXXXXX/")
        @NotBlank
        @Size(max = 1024)
        String firstPostUrl,

        /**
         * 두번째 필드(선택) — secondContentType이 있는 캠페인이면 입력받아야 함
         */

        @Schema(description = "두번째 2차 미디어 URL 리스트(옵션)")
        List<String> secondMediaUrls,

        @Schema(description = "두번째 2차 캡션+해시태그(옵션)")
        @Size(max = 2200)
        String secondCaptionWithHashtags,

        @Schema(description = "두번째 2차 게시물 URL(옵션)")
        @Size(max = 1024)
        String secondPostUrl
) {
}
