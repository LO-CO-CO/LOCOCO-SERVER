package com.lokoko.domain.brand.api.dto.request;

import java.util.List;

public record CreatorApproveRequest(
        List<Long> applicationIds
) {
}
