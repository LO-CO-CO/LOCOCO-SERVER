package com.lokoko.global.common.enums;


import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.Locale;

@Getter
public enum Language {
    EN(1, Locale.ENGLISH),
    JP(2, Locale.JAPANESE),
    ES(3, new Locale("es"));

    private final Integer code;
    private final Locale locale;

    Language(Integer code, Locale locale) {
        this.code = code;
        this.locale = locale;
    }

    public static final Locale[] fallbackLocales = {
            Locale.ENGLISH,
            Locale.JAPANESE,
            new Locale("es")
    };

    public static Language ofCode(Integer code) {
        return Arrays.stream(Language.values())
                .filter(language -> language.getCode().equals(code))
                .findAny()
                .orElse(EN);
    }

    public static Language ofLocale() {
        String languageStr = LocaleContextHolder.getLocale().getLanguage();
        return Arrays.stream(Language.values())
                .filter(language -> language.getLocale().getLanguage().equals(languageStr))
                .findAny()
                .orElse(EN); // 기본값으로 영어 반환
    }

    public static Language ofLocale(Locale locale) {
        return Arrays.stream(Language.values())
                .filter(language -> language.getLocale().equals(locale))
                .findAny()
                .orElse(EN); // 기본값으로 영어 반환
    }
}