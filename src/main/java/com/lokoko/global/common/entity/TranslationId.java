package com.lokoko.global.common.entity;

import com.lokoko.global.common.converter.LanguageConverter;
import com.lokoko.global.common.enums.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class TranslationId implements Serializable {

    @Column(name = "translation_key", nullable = false)
    @Comment("번역 키")
    private String key;

    @Convert(converter = LanguageConverter.class)
    @Column(name = "language", nullable = false)
    @Comment("언어")
    private Language language;

    public TranslationId(String key, Language language) {
        this.key = key;
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationId that = (TranslationId) o;
        return Objects.equals(key, that.key) && language == that.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, language);
    }
}
