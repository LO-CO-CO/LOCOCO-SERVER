package com.lokoko.global.config;

import com.deepl.api.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DeepL API 설정
 */
@Slf4j
@Configuration
public class DeepLConfig {

    @Value("${deepl.api.key:}")
    private String apiKey;

    @Bean
    public Translator deepLTranslator() {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("DeepL API key is not configured. Translation service will work in mock mode.");
            return null;
        }
        log.info("DeepL API configured successfully");
        return new Translator(apiKey);
    }
}