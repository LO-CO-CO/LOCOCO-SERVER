package com.lokoko.domain.product.application.event;

import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NewProductsCacheEvictEvent {
    private final MiddleCategory middleCategory;
}