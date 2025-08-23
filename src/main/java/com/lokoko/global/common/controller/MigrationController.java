package com.lokoko.global.common.controller;

import com.lokoko.global.common.response.ApiResponse;
import com.lokoko.global.common.service.ProductTranslationMigrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<String>> migrateProductTranslations() {
        log.info("Starting product translation migration via API");
        
        try {
            migrationService.migrateAllProducts();
            return ResponseEntity.ok(ApiResponse.success(
                    HttpStatus.OK,
                    "Product translation migration completed successfully",
                    "Migration completed"
            ));
        } catch (Exception e) {
            log.error("Migration failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Migration failed: " + e.getMessage(),
                            "migration failed"
                    ));
        }
    }

    /**
     * 특정 Product의 번역 데이터 마이그레이션
     */
    @PostMapping("/products/translations/{productId}")
    @Operation(summary = "특정 Product 번역 데이터 마이그레이션", 
               description = "특정 Product ID의 brand_name, ingredients, product_detail, product_name을 Translation 테이블로 마이그레이션")
    public ResponseEntity<ApiResponse<String>> migrateSingleProduct(@PathVariable Long productId) {
        log.info("Starting single product translation migration for ID={} via API", productId);
        
        try {
            // 마이그레이션 수행
            migrationService.migrateSingleProduct(productId);
            
            // 검증은 별도 트랜잭션에서 수행
            boolean isValid = migrationService.validateSingleProduct(productId);
            
            String message = isValid 
                ? "Product translation migration completed and validated successfully for ID=" + productId
                : "Product translation migration completed but validation failed for ID=" + productId;
            
            return ResponseEntity.ok(ApiResponse.success(
                    HttpStatus.OK,
                    message,
                    "Migration completed for product ID=" + productId
            ));
        } catch (Exception e) {
            log.error("Migration failed for product ID={}: ", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Migration failed for product ID=" + productId + ": " + e.getMessage(),
                            "migration failed"
                    ));
        }
    }

    /**
     * 마이그레이션 결과 검증
     */
    @PostMapping("/products/translations/validate")
    @Operation(summary = "Product 번역 마이그레이션 검증", 
               description = "모든 Product의 번역이 제대로 마이그레이션 되었는지 검증")
    public ResponseEntity<ApiResponse<String>> validateMigration() {
        log.info("Starting migration validation via API");
        
        try {
            migrationService.validateMigration();
            return ResponseEntity.ok(ApiResponse.success(
                    HttpStatus.OK,
                    "Migration validation completed. Check logs for details.",
                    "Validation completed"
            ));
        } catch (Exception e) {
            log.error("Validation failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Validation failed: " + e.getMessage(),
                            "migration failed"
                    ));
        }
    }
}