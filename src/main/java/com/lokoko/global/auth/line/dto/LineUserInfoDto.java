package com.lokoko.global.auth.line.dto;

public record LineUserInfoDto(
        String sub,
        String name,
        String picture
) {
}
