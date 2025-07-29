package com.lokoko.domain.product.api.dto.response;

import java.util.List;

public record VideoCrawlResponse(
        List<String> videoUrls
) {
    public static VideoCrawlResponse of(List<String> videoUrls) {
        return new VideoCrawlResponse(videoUrls);
    }
}
