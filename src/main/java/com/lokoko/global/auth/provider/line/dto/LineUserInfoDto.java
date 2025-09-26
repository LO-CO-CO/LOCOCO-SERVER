package com.lokoko.global.auth.provider.line.dto;

public record LineUserInfoDto(
        String sub,
        String name,
        String picture
) {
}
