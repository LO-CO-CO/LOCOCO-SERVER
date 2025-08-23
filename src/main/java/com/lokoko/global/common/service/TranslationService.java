package com.lokoko.global.common.service;

import com.lokoko.global.common.entity.Translation;
import com.lokoko.global.common.entity.TranslationId;
import com.lokoko.global.common.enums.Language;
import com.lokoko.global.common.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranslationService {

    private final TranslationRepository translationRepository;

    /**
     * 키와 특정 언어로 번역 조회
     */
    public String getTranslation(String key, Language language) {
        Optional<Translation> translation = translationRepository.findByKeyAndLanguage(key, language);
        return translation.map(Translation::getValue).orElse(null);
    }

    /**
     * 키와 언어로 번역 조회 (fallback 지원)
     */
    public String getTranslationWithFallback(String key) {
        Language currentLanguage = Language.ofLocale();
        return getTranslationWithFallback(key, currentLanguage);
    }

    /**
     * 키와 언어로 번역 조회 (fallback 지원)
     */
    public String getTranslationWithFallback(String key, Language language) {
        List<Translation> translations = translationRepository.findByKey(key);
        TranslationId translationId = new TranslationId(key, language);
        return getTranslationWithFallback(translationId, translations);
    }

    /**
     * fallback 로직을 사용한 번역 조회
     */
    private String getTranslationWithFallback(TranslationId translationId, List<Translation> translations) {
        // 먼저 요청된 언어로 번역 찾기
        Optional<Translation> translation = translations.stream()
                .filter(t -> t.getTranslationId().equals(translationId))
                .findFirst();

        if (translation.isPresent()) {
            return translation.get().getValue();
        }

        // fallback 언어들로 번역 찾기
        for (Locale locale : Language.fallbackLocales) {
            Language fallbackLanguage = Language.ofLocale(locale);
            TranslationId fallbackId = new TranslationId(translationId.getKey(), fallbackLanguage);
            
            translation = translations.stream()
                    .filter(t -> t.getTranslationId().equals(fallbackId))
                    .findFirst();

            if (translation.isPresent()) {
                log.debug("Using fallback translation for key: {} with language: {}", 
                         translationId.getKey(), fallbackLanguage);
                return translation.get().getValue();
            }
        }

        log.warn("No translation found for key: {} with any fallback language", translationId.getKey());
        return null;
    }

    /**
     * 여러 키에 대한 번역들을 Map으로 반환
     */
    public Map<String, String> getTranslations(List<String> keys) {
        Language currentLanguage = Language.ofLocale();
        return getTranslations(keys, currentLanguage);
    }

    /**
     * 여러 키에 대한 특정 언어 번역들을 Map으로 반환
     */
    public Map<String, String> getTranslations(List<String> keys, Language language) {
        List<Translation> translations = translationRepository.findByKeys(keys);
        
        return keys.stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> {
                            TranslationId translationId = new TranslationId(key, language);
                            List<Translation> keyTranslations = translations.stream()
                                    .filter(t -> t.getKey().equals(key))
                                    .collect(Collectors.toList());
                            return getTranslationWithFallback(translationId, keyTranslations);
                        }
                ));
    }

    /**
     * 번역 저장/업데이트
     */
    @Transactional
    public Translation saveTranslation(String key, Language language, String value) {
        Translation translation = new Translation(key, language, value);
        return translationRepository.save(translation);
    }

    /**
     * 여러 번역 일괄 저장/업데이트
     */
    @Transactional
    public List<Translation> saveTranslations(List<Translation> translations) {
        return translationRepository.saveAll(translations);
    }

    /**
     * 번역 삭제
     */
    @Transactional
    public void deleteTranslation(String key, Language language) {
        TranslationId translationId = new TranslationId(key, language);
        translationRepository.deleteById(translationId);
    }

    /**
     * 키의 모든 번역 삭제
     */
    @Transactional
    public void deleteAllTranslationsByKey(String key) {
        List<Translation> translations = translationRepository.findByKey(key);
        translationRepository.deleteAll(translations);
    }

    /**
     * 키 패턴으로 번역 검색
     */
    public List<Translation> findByKeyPattern(String pattern) {
        return translationRepository.findByKeyPattern("%" + pattern + "%");
    }

    public Map<String, String> getBatchTranslations(List<String> keys, Language language) {
        // 단일 쿼리로 모든 번역 조회
        List<Translation> translations = translationRepository.findByKeysAndLanguage(keys, language);
        
        // 조회된 번역을 Map으로 변환
        Map<String, String> translationMap = translations.stream()
                .collect(Collectors.toMap(
                        Translation::getKey,
                        Translation::getValue,
                        (existing, replacement) -> existing // 중복 키 처리
                ));
        
        // 번역이 없는 키들에 대해 null 값 설정
        return keys.stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> translationMap.getOrDefault(key, null)
                ));
    }
    
    /**
     * 문자열 언어 코드를 Language enum으로 변환
     * @param lang 언어 코드 문자열 (JP, EN, ES)
     * @return Language enum, 잘못된 값이면 JP 반환
     */
    public static Language parseLanguage(String lang) {
        if (lang == null || lang.isEmpty()) {
            return Language.JP; // 기본값
        }
        
        try {
            return Language.valueOf(lang.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid language code: {}. Using default JP", lang);
            return Language.JP;
        }
    }
}