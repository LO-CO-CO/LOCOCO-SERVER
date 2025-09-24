package com.lokoko.global.common.converter;

import com.lokoko.global.common.enums.Language;

import jakarta.persistence.*;

@Converter(autoApply = true)
public class LanguageConverter implements AttributeConverter<Language, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Language language) {
        if (language == null) {
            return null;
        }
        return language.getCode();
    }

    @Override
    public Language convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }
        return Language.ofCode(code);
    }
}