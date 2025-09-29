package com.lokoko.domain.brand.api.dto.response;


import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BrandIssuedCampaignResponse(

        @Schema(requiredMode = REQUIRED, description = "생성한 캠페인 ID", example = "11")
        Long campaignId,

        @Schema(requiredMode = REQUIRED, description = "생성한 캠페인 제목", example = "Summer Hydration Campaign")
        String title,

        @Schema(requiredMode = REQUIRED, description = "브랜드가 지정한 1번째 리뷰 컨텐츠 타입(캠페인 설정)", example = "INSTA_REELS")
        ContentType firstContentPlatform,

        @Schema(description = "브랜드가 지정한 2번째 리뷰 컨텐츠 타입(없을 수 있음)", example = "TIKTOK_VIDEO")
        ContentType secondContentPlatform,

        @Schema(description = "브랜드 노트(본인이 작성한 내용이 있다면 반환, 리스트에서는 보통 null)")
        String brandNote,

        @Schema(description = "브랜드 노트 작성 시간(본인이 작성한 내용이 있다면 반환, 리스트에서는 보통 null)",
                example = "2023-10-05T14:48:00Z")
        Instant revisionRequestedAt

) {
}
