package com.lokoko.domain.productBrand.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lokoko.domain.productBrand.api.dto.message.ResponseMessage;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandInfoListResponse;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandNameListResponse;
import com.lokoko.domain.productBrand.application.usecase.ProductBrandUsecase;
import com.lokoko.global.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "PRODUCT BRAND")
@RestController
@RequestMapping("/api/product-brand")
@RequiredArgsConstructor
public class ProductBrandController {

	private final ProductBrandUsecase productBrandUsecase;

	@Operation(summary = "상품 브랜드 이름 리스트 조회 (전체 / A~Z / 0)")
	@GetMapping
	public ApiResponse<ProductBrandNameListResponse> getProductBrandNames(
		@RequestParam(required = false) String startsWith
	) {
		ProductBrandNameListResponse response = productBrandUsecase.getBrandNames(startsWith);

		return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_BRAND_GET_SUCCESS.getMessage(), response);
	}

	@Operation(summary = "브랜드 이름으로 상품 목록 조회 (페이지네이션, 별점 높은 순)")
	@GetMapping("/products")
	public ApiResponse<ProductBrandInfoListResponse> getProductsByBrandName(
		@RequestParam(required = false) String productBrandName,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "12") int size
	) {
		ProductBrandInfoListResponse response = productBrandUsecase.getProductsByBrandName(productBrandName, page,
			size);

		return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_LIST_FETCH_SUCCESS.getMessage(), response);
	}
}