package com.lokoko.domain.productBrand.api;

import com.lokoko.domain.productBrand.api.dto.message.ResponseMessage;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandNameListResponse;
import com.lokoko.domain.productBrand.application.mapper.ProductBrandMapper;
import com.lokoko.domain.productBrand.application.service.ProductBrandGetService;
import com.lokoko.domain.productBrand.domain.entity.ProductBrand;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "PRODUCT BRAND")
@RestController
@RequestMapping("/api/product-brand")
@RequiredArgsConstructor
public class ProductBrandController {

    private final ProductBrandGetService productBrandGetService;
    private final ProductBrandMapper productBrandMapper;

    @Operation(summary = "상품 브랜드 이름 리스트 조회 (전체 / A~Z / 0)")
    @GetMapping
    public ApiResponse<ProductBrandNameListResponse> getProductBrandNames(
            @RequestParam(required = false) String startsWith
    ) {
        List<ProductBrand> brands = productBrandGetService.getBrandNames(startsWith);
        ProductBrandNameListResponse response = productBrandMapper.toBrandNameListResponse(brands);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_BRAND_GET_SUCCESS.getMessage(), response);
    }
}