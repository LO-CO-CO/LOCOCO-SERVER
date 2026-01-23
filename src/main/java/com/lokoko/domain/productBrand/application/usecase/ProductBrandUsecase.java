package com.lokoko.domain.productBrand.application.usecase;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lokoko.domain.productBrand.api.dto.ProductBrandInfoProjection;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandInfoListResponse;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandInfoResponse;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandNameListResponse;
import com.lokoko.domain.productBrand.application.mapper.ProductBrandMapper;
import com.lokoko.domain.productBrand.application.service.ProductBrandGetService;
import com.lokoko.domain.productBrand.domain.entity.ProductBrand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductBrandUsecase {

	private final ProductBrandGetService productBrandGetService;

	private final ProductBrandMapper productBrandMapper;

	@Transactional(readOnly = true)
	public ProductBrandNameListResponse getBrandNames(String startsWith) {
		List<ProductBrand> brands = productBrandGetService.getBrandNames(startsWith);
		return productBrandMapper.toBrandNameListResponse(brands);
	}

	@Transactional(readOnly = true)
	public ProductBrandInfoListResponse getProductsByBrandName(String productBrandName, int page, int size) {

		Slice<ProductBrandInfoProjection> slice = productBrandGetService.getProductsByBrandName(productBrandName, page,
			size);
		Long totalElements = productBrandGetService.countProductsByBrandName(productBrandName);

		List<ProductBrandInfoResponse> products = slice.getContent().stream()
			.map(productBrandMapper::toProductBrandInfoResponse)
			.toList();

		return productBrandMapper.toProductBrandInfoList(slice, products, totalElements);
	}
}
