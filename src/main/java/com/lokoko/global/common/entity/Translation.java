package com.lokoko.global.common.entity;

import com.lokoko.global.common.enums.Language;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "translation", indexes = {
        @Index(name = "idx_translation_key", columnList = "translation_key")
})
public class Translation {

    @EmbeddedId
    private TranslationId translationId;


    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("번역 값")
    private String value;

    public Translation(TranslationId translationId, String value) {
        this.translationId = translationId;
        this.value = value;
    }

    public Translation(String key, Language language, String value) {
        this.translationId = new TranslationId(key, language);
        this.value = value;
    }

    public Language getLanguage() {
        return translationId.getLanguage();
    }

    public String getKey() {
        return translationId.getKey();
    }
}
