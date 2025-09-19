package com.lokoko.domain.campaign.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LanguageFilter {
    ENG("Eng"),
    ESN("Esn"),
    ALL("All");

    private final String displayName;
}
