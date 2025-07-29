package com.lokoko.domain.youtube.dto;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.domain.video.domain.entity.YoutubeVideo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record TrendsYoutubeResponse(
        @Schema(requiredMode = REQUIRED)
        Long id,
        @Schema(requiredMode = REQUIRED)
        String topic,
        @Schema(requiredMode = REQUIRED)
        String title,
        @Schema(requiredMode = REQUIRED)
        String url,
        @Schema(requiredMode = REQUIRED)
        Integer popularity,
        @Schema(requiredMode = REQUIRED)
        Long viewCount,
        @Schema(requiredMode = REQUIRED)
        LocalDateTime uploadedAt
) {
    public static TrendsYoutubeResponse from(YoutubeVideo video) {
        return new TrendsYoutubeResponse(
                video.getId(),
                video.getTopic(),
                video.getTitle(),
                video.getUrl(),
                video.getPopularity(),
                video.getViewCount(),
                video.getUploadedAt()
        );
    }
}
