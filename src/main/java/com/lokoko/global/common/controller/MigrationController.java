package com.lokoko.global.common.controller;

import com.lokoko.global.common.controller.enums.ResponseMessage;
import com.lokoko.global.common.response.ApiResponse;
import com.lokoko.global.common.service.ProductTranslationMigrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 데이터 마이그레이션 관련 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/migration")
@RequiredArgsConstructor
@Tag(name = "Migration", description = "번역 마이그레이션 API")
public class MigrationController {

    private final ProductTranslationMigrationService migrationService;

    /**
     * Product 테이블의 번역 대상 컬럼을 Translation 테이블로 마이그레이션
     */
    @PostMapping("/products/translations")
    @Operation(summary = "Product 번역 데이터 마이그레이션",
            description = "Product 테이블의 brand_name, ingredients, product_detail, product_name을 Translation 테이블로 마이그레이션")
    public ApiResponse<Void> migrateProductTranslations() {

        migrationService.migrateAllProducts();
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_TRANSLATION_SUCCESS.getMessage(), null);
    }

    /**
     * 특정 Product의 번역 데이터 마이그레이션
     */
    @PostMapping("/products/translations/{productId}")
    @Operation(summary = "특정 Product 번역 데이터 마이그레이션",
            description = "특정 Product ID의 brand_name, ingredients, product_detail, product_name을 Translation 테이블로 마이그레이션")
    public ApiResponse<Void> migrateSingleProduct(@PathVariable Long productId) {
        log.info("Starting single product translation migration for ID={} via API", productId);
        // 마이그레이션 수행
        migrationService.migrateSingleProduct(productId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_TRANSLATION_SUCCESS.getMessage(), null);
    }

    /**
     * 마이그레이션 결과 검증
     */
    @PostMapping("/products/translations/validate")
    @Operation(summary = "Product 번역 마이그레이션 검증",
            description = "모든 Product의 번역이 제대로 마이그레이션 되었는지 검증")
    public ApiResponse<Void> validateMigration() {
        log.info("Starting migration validation via API");
        migrationService.validateMigration();
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.TRANSLATION_VALIDATION_SUCCESS.getMessage(), null);
    }
}