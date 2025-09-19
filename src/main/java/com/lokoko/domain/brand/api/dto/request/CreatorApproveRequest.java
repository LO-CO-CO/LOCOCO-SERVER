package com.lokoko.domain.brand.api.dto.request;

import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatorApproveRequest(
        @Size(min = 1 , message = "승인하려는 지원자의 수는 최소 1명 이상이어야합니다.")
        List<Long> applicationIds
) {
}
