package com.lokoko.global.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH); // 기본 Locale 설정
        
        // 지원하는 언어 목록 설정 (영어, 일본어, 스페인어)
        localeResolver.setSupportedLocales(Arrays.asList(
                Locale.ENGLISH,
                Locale.JAPANESE,
                new Locale("es")
        ));
        
        return localeResolver;
    }
}