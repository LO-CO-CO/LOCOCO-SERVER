package com.lokoko.global.auth.provider.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleProfileDto(
        @JsonProperty("sub")
        String userId,        // 구글 사용자 ID
        String name,
        String givenName,
        String familyName,
        String picture,
        String email
) {
}