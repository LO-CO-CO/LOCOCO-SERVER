package com.lokoko.domain.campaignReview.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CompletedReviewResponse(
        @Schema(description = "참여한 캠페인 ID", example = "61")
        Long campaignId,

        @Schema(description = "캠페인 이름", example = "신상품 홍보 캠페인")
        String campaignName,

        @Schema(description = "완료된 리뷰 컨텐츠 목록")
        List<CompletedReviewContent> reviewContents
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CompletedReviewContent(
            @Schema(description = "컨텐츠 타입", example = "INSTA_REELS")
            ContentType contentType,

            @Schema(description = "최종 제출한 캡션과 해시태그", example = "수정해서 제출합니다 ㅎ")
            String captionWithHashtags,

            @Schema(description = "최종 제출한 미디어 URL 목록")
            List<String> mediaUrls
    ) {}
}