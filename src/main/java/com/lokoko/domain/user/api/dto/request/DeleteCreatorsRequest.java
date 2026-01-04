package com.lokoko.domain.user.api.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteCreatorsRequest(
        @NotEmpty(message = "한 개 이상의 ID는 필수입니다")
        List<Long> creatorIds
) {
}
