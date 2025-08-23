package com.lokoko.global.common.repository;

import com.lokoko.global.common.entity.Translation;
import com.lokoko.global.common.entity.TranslationId;
import com.lokoko.global.common.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, TranslationId> {

    /**
     * 키로 모든 번역 조회
     */
    @Query("SELECT t FROM Translation t WHERE t.translationId.key = :key")
    List<Translation> findByKey(@Param("key") String key);

    /**
     * 키와 언어로 번역 조회
     */
    @Query("SELECT t FROM Translation t WHERE t.translationId.key = :key AND t.translationId.language = :language")
    Optional<Translation> findByKeyAndLanguage(@Param("key") String key, @Param("language") Language language);

    /**
     * 여러 키에 대한 번역들 조회
     */
    @Query("SELECT t FROM Translation t WHERE t.translationId.key IN :keys")
    List<Translation> findByKeys(@Param("keys") List<String> keys);

    /**
     * 특정 언어의 모든 번역 조회
     */
    @Query("SELECT t FROM Translation t WHERE t.translationId.language = :language")
    List<Translation> findByLanguage(@Param("language") Language language);

    /**
     * 키 패턴으로 번역 검색
     */
    @Query("SELECT t FROM Translation t WHERE t.translationId.key LIKE :pattern")
    List<Translation> findByKeyPattern(@Param("pattern") String pattern);
    
    /**
     * 여러 키와 특정 언어에 대한 번역들 조회
     */
    @Query("SELECT t FROM Translation t WHERE t.translationId.key IN :keys AND t.translationId.language = :language")
    List<Translation> findByKeysAndLanguage(@Param("keys") List<String> keys, @Param("language") Language language);
}