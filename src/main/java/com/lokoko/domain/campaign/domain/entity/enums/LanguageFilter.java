package com.lokoko.domain.campaign.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LanguageFilter {
    EN("EN"),
    ES("ES"),
    ALL("ALL");

    private final String displayName;
}
