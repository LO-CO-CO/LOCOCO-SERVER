package com.lokoko.global.utils;

import com.lokoko.global.common.entity.Translation;
import com.lokoko.global.common.entity.TranslationId;
import com.lokoko.global.common.enums.Language;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 번역 해결을 위한 유틸리티 클래스
 * fallback 로직을 포함한 번역 값 조회 기능 제공
 */
@Slf4j
public record TranslationResolver(TranslationId translationId, List<Translation> translations) {

    /**
     * fallback을 지원하는 번역 조회
     */
    public String getTranslationWithFallback() {
        // 먼저 요청된 언어로 번역 찾기
        Optional<Translation> translationOptional = translations.stream()
                .filter(translation -> translation.getTranslationId().equals(this.translationId))
                .findFirst();

        if (translationOptional.isPresent()) {
            return translationOptional.get().getValue();
        }

        // fallback 언어들로 번역 찾기
        for (Locale locale : Language.fallbackLocales) {
            TranslationId fallbackId = generateTranslationId(locale);
            translationOptional = translations.stream()
                    .filter(translation -> translation.getTranslationId().equals(fallbackId))
                    .findFirst();

            if (translationOptional.isPresent()) {
                log.debug("Using fallback translation for key: {} with language: {}", 
                         this.translationId.getKey(), Language.ofLocale(locale));
                return translationOptional.get().getValue();
            }
        }

        log.warn("No translation found for key: {} with any fallback language", this.translationId.getKey());
        return null;
    }

    /**
     * 정확한 언어 매치만 조회 (fallback 없음)
     */
    public String getTranslation() {
        Optional<Translation> translationOptional = translations.stream()
                .filter(translation -> translation.getTranslationId().equals(this.translationId))
                .findFirst();
        return translationOptional.map(Translation::getValue).orElse(null);
    }

    /**
     * 특정 Locale에 대한 TranslationId 생성
     */
    public TranslationId generateTranslationId(Locale locale) {
        return new TranslationId(this.translationId.getKey(), Language.ofLocale(locale));
    }

    /**
     * 정적 메서드로 fallback을 지원하는 번역 조회
     */
    public static String getTranslationWithFallback(TranslationId translationId, List<Translation> translations) {
        TranslationResolver translationResolver = new TranslationResolver(translationId, translations);
        return translationResolver.getTranslationWithFallback();
    }

    /**
     * 정적 메서드로 정확한 언어 매치만 조회
     */
    public static String getTranslation(TranslationId translationId, List<Translation> translations) {
        TranslationResolver translationResolver = new TranslationResolver(translationId, translations);
        return translationResolver.getTranslation();
    }

    /**
     * 현재 Locale을 기반으로 한 번역 조회
     */
    public static String getTranslationWithFallback(String key, List<Translation> translations) {
        Language currentLanguage = Language.ofLocale();
        TranslationId translationId = new TranslationId(key, currentLanguage);
        return getTranslationWithFallback(translationId, translations);
    }

    /**
     * 특정 언어로 번역 조회
     */
    public static String getTranslationWithFallback(String key, Language language, List<Translation> translations) {
        TranslationId translationId = new TranslationId(key, language);
        return getTranslationWithFallback(translationId, translations);
    }
}