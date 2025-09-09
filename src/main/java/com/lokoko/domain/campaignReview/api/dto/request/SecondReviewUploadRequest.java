package com.lokoko.domain.campaignReview.api.dto.request;


import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.socialclip.domain.entity.enums.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SecondReviewUploadRequest(

        /**
         * 두번째 리뷰 업로드시 입력받을 필드들 (명시적으로 다 입력 받기)
         */

        @Schema(description = "콘텐츠 포맷", example = "INSTA_REELS")
        @NotNull
        Content content,

        @Schema(description = "캠페인 리뷰 이미지 URL 리스트", example = "[\"https://s3.example.com/review/img1.jpg\", \"https://s3.example.com/review/img2.jpg\"]")
        @NotBlank
        List<String> imageUrls,

        @Schema(description = "캡션+해시태그 (최대 2200자)", example = "Hydrating mask review 💧 #hydration #mask #skincare")
        @NotBlank
        @Size(max = 2200)
        String captionWithHashtags,

        @Schema(requiredMode = REQUIRED, description = "게시물 URL", example = "https://www.instagram.com/p/XXXXXXXX/")
        @NotBlank
        @Size(max = 1024)
        String postUrl,

        @Schema(description = "브랜드 수정 요청 사항", example = "Please mention the brand's formula more in the video and add the hashtag #glassskin.")
        @NotBlank
        @Size(max = 1000)
        String brandNote
) {
}
