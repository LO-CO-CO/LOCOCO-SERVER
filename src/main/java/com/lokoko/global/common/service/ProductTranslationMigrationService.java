package com.lokoko.global.common.service;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.global.common.entity.Translation;
import com.lokoko.global.common.enums.Language;
import com.lokoko.global.common.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Product 테이블의 일본어 데이터를 Translation 테이블로 마이그레이션하는 서비스
 * brand_name, ingredients, product_detail, product_name 4개 컬럼을 마이그레이션
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductTranslationMigrationService {

    private final ProductRepository productRepository;
    private final TranslationRepository translationRepository;
    
    @Autowired(required = false)
    private Translator deepLTranslator;

    /**
     * 모든 Product의 번역 대상 컬럼을 Translation 테이블로 마이그레이션
     */
    public void migrateAllProducts() {
        log.info("Starting product translation migration...");
        
        try {
            // 먼저 카운트 쿼리로 확인
            log.info("Counting products in database...");
            long productCount = productRepository.count();
            log.info("Total product count: {}", productCount);
            
            if (productCount == 0) {
                log.warn("No products found to migrate");
                return;
            }
            
            log.info("Fetching all products from database...");
            List<Product> products = productRepository.findAll();
            log.info("Product query executed successfully. Found {} products", products.size());
            
            int totalProducts = products.size();
            int processedCount = 0;
            int failedCount = 0;
            
            log.info("Starting migration for {} products", totalProducts);
            
            for (int i = 0; i < totalProducts; i++) {
                Product product = products.get(i);
                try {
                    log.info("Migrating product {}/{}: ID={}, Name={}", 
                            i + 1, totalProducts, product.getId(), 
                            product.getProductName() != null ? 
                                product.getProductName().substring(0, Math.min(30, product.getProductName().length())) : "N/A");
                    
                    migrateProductInNewTransaction(product.getId());
                    processedCount++;
                    
                    // 10개마다 진행 상황 로그
                    if ((i + 1) % 10 == 0) {
                        log.info("Progress: {}/{} products migrated", i + 1, totalProducts);
                    }
                } catch (Exception e) {
                    failedCount++;
                    log.error("Failed to migrate product {}: {}", product.getId(), e.getMessage());
                }
            }
            
            log.info("Migration completed. Successfully migrated: {}/{} products, Failed: {}", 
                    processedCount, totalProducts, failedCount);
        } catch (Exception e) {
            log.error("Fatal error during migration: {}", e.getMessage(), e);
            throw new RuntimeException("Migration failed", e);
        }
    }
    
    /**
     * 새로운 트랜잭션에서 단일 Product 마이그레이션
     */
    @Transactional
    public void migrateProductInNewTransaction(Long productId) {
        log.debug("Starting migration for product ID={}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        migrateProduct(product);
    }
    
    /**
     * 특정 Product ID에 대한 번역 마이그레이션 (단일 제품)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 30)
    public void migrateSingleProduct(Long productId) {
        log.info("Starting single product migration for ID={}", productId);
        
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
            
            log.info("Found product: ID={}, Name={}", 
                    product.getId(), 
                    product.getProductName() != null ? 
                        product.getProductName().substring(0, Math.min(50, product.getProductName().length())) : "N/A");
            
            // 기존 번역 삭제 (있다면)
            deleteExistingTranslations(productId);
            
            // 새로운 번역 생성
            migrateProduct(product);
            
            log.info("Successfully migrated product ID={}", productId);
            
        } catch (Exception e) {
            log.error("Failed to migrate product ID={}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Migration failed for product ID=" + productId, e);
        }
    }
    
    /**
     * 마이그레이션 후 검증 (별도 트랜잭션)
     */
    @Transactional(readOnly = true)
    public boolean validateSingleProduct(Long productId) {
        log.info("Validating product ID={}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        
        boolean isValid = validateProduct(product);
        if (isValid) {
            log.info("Validation passed for product ID={}", productId);
        } else {
            log.warn("Validation failed for product ID={}", productId);
        }
        return isValid;
    }
    
    /**
     * 기존 번역 삭제
     */
    private void deleteExistingTranslations(Long productId) {
        log.info("Deleting existing translations for product ID={}", productId);
        
        // brand_name 삭제
        String brandKey = "product_brand_" + productId;
        List<Translation> brandTranslations = translationRepository.findByKey(brandKey);
        if (!brandTranslations.isEmpty()) {
            translationRepository.deleteAll(brandTranslations);
            log.debug("Deleted {} brand translations", brandTranslations.size());
        }
        
        // ingredients 삭제
        String ingredientsKey = "product_ingredients_" + productId;
        List<Translation> ingredientsTranslations = translationRepository.findByKey(ingredientsKey);
        if (!ingredientsTranslations.isEmpty()) {
            translationRepository.deleteAll(ingredientsTranslations);
            log.debug("Deleted {} ingredients translations", ingredientsTranslations.size());
        }
        
        // product_detail 삭제
        String detailKey = "product_detail_" + productId;
        List<Translation> detailTranslations = translationRepository.findByKey(detailKey);
        if (!detailTranslations.isEmpty()) {
            translationRepository.deleteAll(detailTranslations);
            log.debug("Deleted {} detail translations", detailTranslations.size());
        }
        
        // product_name 삭제
        String nameKey = "product_name_" + productId;
        List<Translation> nameTranslations = translationRepository.findByKey(nameKey);
        if (!nameTranslations.isEmpty()) {
            translationRepository.deleteAll(nameTranslations);
            log.debug("Deleted {} name translations", nameTranslations.size());
        }
    }

    /**
     * 단일 Product의 번역 대상 컬럼을 Translation 테이블로 마이그레이션
     */
    private void migrateProduct(Product product) {
        Long productId = product.getId();
        
        // 1. brand_name 마이그레이션
        if (product.getBrandName() != null && !product.getBrandName().isEmpty()) {
            String brandKey = "product_brand_" + productId;
            createTranslations(brandKey, product.getBrandName());
        }
        
        // 2. ingredients 마이그레이션
        if (product.getIngredients() != null && !product.getIngredients().isEmpty()) {
            String ingredientsKey = "product_ingredients_" + productId;
            createTranslations(ingredientsKey, product.getIngredients());
        }
        
        // 3. product_detail 마이그레이션
        if (product.getProductDetail() != null && !product.getProductDetail().isEmpty()) {
            String detailKey = "product_detail_" + productId;
            createTranslations(detailKey, product.getProductDetail());
        }
        
        // 4. product_name 마이그레이션
        if (product.getProductName() != null && !product.getProductName().isEmpty()) {
            String nameKey = "product_name_" + productId;
            createTranslations(nameKey, product.getProductName());
        }
    }

    /**
     * 주어진 키와 일본어 텍스트로 일본어, 영어, 스페인어 번역 생성
     */
    private void createTranslations(String key, String japaneseText) {
        log.debug("Creating translations for key: {}", key);
        List<Translation> translations = new ArrayList<>();
        
        // 1. 일본어 원본 저장
        Translation jpTranslation = new Translation(key, Language.JP, japaneseText);
        translations.add(jpTranslation);
        log.debug("Added Japanese translation for key: {}", key);
        
        // 2. 영어 번역
        log.debug("Translating to English for key: {}", key);
        String englishText = translateToEnglish(japaneseText);
        Translation enTranslation = new Translation(key, Language.EN, englishText);
        translations.add(enTranslation);
        log.debug("Added English translation for key: {}", key);
        
        // 3. 스페인어 번역
        log.debug("Translating to Spanish for key: {}", key);
        String spanishText = translateToSpanish(japaneseText);
        Translation esTranslation = new Translation(key, Language.ES, spanishText);
        translations.add(esTranslation);
        log.debug("Added Spanish translation for key: {}", key);
        
        // 일괄 저장
        log.debug("Saving {} translations for key: {}", translations.size(), key);
        translationRepository.saveAll(translations);
        log.debug("Successfully saved translations for key: {}", key);
    }

    /**
     * 일본어를 영어로 번역 (DeepL API 사용)
     */
    private String translateToEnglish(String japaneseText) {
        if (deepLTranslator == null) {
            log.debug("DeepL translator is not available. Using mock translation for EN.");
            return "[EN-MOCK] " + japaneseText;
        }
        
        try {
            // 텍스트 길이 제한 (DeepL API 제한 고려)
            String textToTranslate = japaneseText;
            if (japaneseText.length() > 5000) {
                textToTranslate = japaneseText.substring(0, 5000);
                log.warn("Text truncated for translation: original length={}", japaneseText.length());
            }
            
            // DeepL API를 사용하여 일본어를 영어로 번역
            TextResult result = deepLTranslator.translateText(
                textToTranslate,
                "ja",  // 소스 언어: 일본어
                "en-US" // 타겟 언어: 영어 (미국)
            );
            
            String translatedText = result.getText();
            log.debug("Translated JP to EN: {} -> {}", 
                     textToTranslate.substring(0, Math.min(50, textToTranslate.length())), 
                     translatedText.substring(0, Math.min(50, translatedText.length())));
            
            return translatedText;
        } catch (DeepLException e) {
            log.warn("Failed to translate to English: {}", e.getMessage());
            // 번역 실패 시 Mock 데이터 반환
            return "[EN-MOCK] " + japaneseText;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Translation interrupted: {}", e.getMessage());
            return "[EN-MOCK] " + japaneseText;
        }
    }

    /**
     * 일본어를 스페인어로 번역 (DeepL API 사용)
     */
    private String translateToSpanish(String japaneseText) {
        if (deepLTranslator == null) {
            log.debug("DeepL translator is not available. Using mock translation for ES.");
            return "[ES-MOCK] " + japaneseText;
        }
        
        try {
            // 텍스트 길이 제한 (DeepL API 제한 고려)
            String textToTranslate = japaneseText;
            if (japaneseText.length() > 5000) {
                textToTranslate = japaneseText.substring(0, 5000);
                log.warn("Text truncated for translation: original length={}", japaneseText.length());
            }
            
            // DeepL API를 사용하여 일본어를 스페인어로 번역
            TextResult result = deepLTranslator.translateText(
                textToTranslate,
                "ja",  // 소스 언어: 일본어
                "es"   // 타겟 언어: 스페인어
            );
            
            String translatedText = result.getText();
            log.debug("Translated JP to ES: {} -> {}", 
                     textToTranslate.substring(0, Math.min(50, textToTranslate.length())), 
                     translatedText.substring(0, Math.min(50, translatedText.length())));
            
            return translatedText;
        } catch (DeepLException e) {
            log.warn("Failed to translate to Spanish: {}", e.getMessage());
            // 번역 실패 시 Mock 데이터 반환
            return "[ES-MOCK] " + japaneseText;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Translation interrupted: {}", e.getMessage());
            return "[ES-MOCK] " + japaneseText;
        }
    }

    /**
     * 마이그레이션 검증 - 모든 제품의 번역이 제대로 되었는지 확인
     */
    @Transactional(readOnly = true)
    public void validateMigration() {
        log.info("Starting migration validation...");
        
        List<Product> products = productRepository.findAll();
        int totalProducts = products.size();
        int validCount = 0;
        List<Long> failedProductIds = new ArrayList<>();
        
        for (Product product : products) {
            boolean isValid = validateProduct(product);
            if (isValid) {
                validCount++;
            } else {
                failedProductIds.add(product.getId());
            }
        }
        
        log.info("Validation completed. Valid: {}/{}", validCount, totalProducts);
        if (!failedProductIds.isEmpty()) {
            log.warn("Failed product IDs: {}", failedProductIds);
        }
    }

    /**
     * 단일 제품의 번역 검증
     */
    private boolean validateProduct(Product product) {
        Long productId = product.getId();
        boolean allValid = true;
        
        // brand_name 검증
        if (product.getBrandName() != null && !product.getBrandName().isEmpty()) {
            String brandKey = "product_brand_" + productId;
            allValid &= validateTranslations(brandKey);
        }
        
        // ingredients 검증
        if (product.getIngredients() != null && !product.getIngredients().isEmpty()) {
            String ingredientsKey = "product_ingredients_" + productId;
            allValid &= validateTranslations(ingredientsKey);
        }
        
        // product_detail 검증
        if (product.getProductDetail() != null && !product.getProductDetail().isEmpty()) {
            String detailKey = "product_detail_" + productId;
            allValid &= validateTranslations(detailKey);
        }
        
        // product_name 검증
        if (product.getProductName() != null && !product.getProductName().isEmpty()) {
            String nameKey = "product_name_" + productId;
            allValid &= validateTranslations(nameKey);
        }
        
        return allValid;
    }

    /**
     * 특정 키에 대해 3개 언어(JP, EN, ES)의 번역이 모두 존재하는지 검증
     */
    private boolean validateTranslations(String key) {
        List<Translation> translations = translationRepository.findByKey(key);
        
        boolean hasJapanese = translations.stream()
                .anyMatch(t -> t.getLanguage() == Language.JP);
        boolean hasEnglish = translations.stream()
                .anyMatch(t -> t.getLanguage() == Language.EN);
        boolean hasSpanish = translations.stream()
                .anyMatch(t -> t.getLanguage() == Language.ES);
        
        if (!hasJapanese || !hasEnglish || !hasSpanish) {
            log.warn("Missing translations for key: {}. JP: {}, EN: {}, ES: {}", 
                    key, hasJapanese, hasEnglish, hasSpanish);
            return false;
        }
        
        return true;
    }
}