package com.lokoko.domain.product.api;

import com.lokoko.domain.product.api.dto.response.NewProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.PopularProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.ProductDetailResponse;
import com.lokoko.domain.product.api.dto.response.ProductYoutubeResponse;
import com.lokoko.domain.product.application.service.ProductReadService;
import com.lokoko.domain.product.domain.entity.enums.ProductCategory;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.lokoko.domain.product.api.message.ResponseMessage.*;

@Tag(name = "PRODUCT")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductReadService productReadService;

    @Operation(summary = "신상품 카테고리별 조회")
    @GetMapping("/categories/new")
    public ApiResponse<NewProductsByCategoryResponse> searchNewProductsByCategory(
            @RequestParam(required = false) ProductCategory productCategory) {

        return ApiResponse.success(HttpStatus.OK, CATEGORY_NEW_LIST_SUCCESS.getMessage(),
                productReadService.searchNewProductsByCategory(productCategory));
    }

    @Operation(summary = "인기상품 카테고리별 조회")
    @GetMapping("/categories/popular")
    public ApiResponse<PopularProductsByCategoryResponse> searchPopularProductsByCategory(
            @RequestParam(required = false) ProductCategory productCategory) {

        return ApiResponse.success(HttpStatus.OK, CATEGORY_POPULAR_LIST_SUCCESS.getMessage(),
                productReadService.searchPopularProductsByCategory(productCategory));
    }

    @Operation(summary = "상세조회 제품(별점 포함) 조회 (상세 조회)")
    @GetMapping("/details/{productId}")
    public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable Long productId,
                                                               @Parameter(hidden = true) @CurrentUser Long userId) {
        ProductDetailResponse detail = productReadService.getProductDetail(productId, userId);

        return ApiResponse.success(HttpStatus.OK, PRODUCT_DETAIL_SUCCESS.getMessage(), detail);
    }

    @Operation(summary = "상세조회 유튜브 리뷰 조회 (상세 조회)")
    @GetMapping("/details/{productId}/youtube")
    public ApiResponse<ProductYoutubeResponse> getProductDetailYoutube(@PathVariable Long productId) {
        ProductYoutubeResponse detailYoutube = productReadService.getProductDetailYoutube(productId);

        return ApiResponse.success(HttpStatus.OK, PRODUCT_YOUTUBE_DETAIL_SUCCESS.getMessage(), detailYoutube);
    }
}
