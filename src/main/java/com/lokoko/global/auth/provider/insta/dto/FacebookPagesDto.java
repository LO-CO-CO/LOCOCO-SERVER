package com.lokoko.global.auth.provider.insta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Facebook Pages 목록 응답 DTO
 */
public record FacebookPagesDto(
        List<FacebookPage> data
) {
    public record FacebookPage(
            String id,
            String name,

            @JsonProperty("access_token")
            String accessToken,

            String category,

            List<String> tasks
    ) {
    }
}
