package com.lokoko.global.auth.line.dto;

public record LineProfileDto(
        String userId,
        String displayName,
        String pictureUrl,
        String statusMessage,
        String email
) {
}
