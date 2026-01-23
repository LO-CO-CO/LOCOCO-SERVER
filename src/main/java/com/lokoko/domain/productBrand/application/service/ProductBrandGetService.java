package com.lokoko.domain.productBrand.application.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.productBrand.api.dto.ProductBrandInfoProjection;
import com.lokoko.domain.productBrand.domain.entity.ProductBrand;
import com.lokoko.domain.productBrand.domain.repository.ProductBrandRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductBrandGetService {

	private final ProductBrandRepository productBrandRepository;
	private final ProductRepository productRepository;

	public List<ProductBrand> getBrandNames(String startsWith) {

		Sort sort = Sort.by(Sort.Direction.ASC, "brandName");

		// 파라미터 없으면 전체
		if (startsWith == null || startsWith.isBlank()) {
			return productBrandRepository.findAll(sort);
		}

		String firstChar = startsWith.trim();

		// 혹시 알파벳 여러 글자 들어온 경우
		String prefix = firstChar.substring(0, 1).toUpperCase();

		if (firstChar.equals("0")) {
			return productBrandRepository.findAllStartsWithDigit();
		}
		return productBrandRepository.findByBrandNameStartingWithIgnoreCase(prefix, sort);
	}

	public Slice<ProductBrandInfoProjection> getProductsByBrandName(String productBrandName, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		return productRepository.findProductsByBrandName(productBrandName, pageable);
	}

	public Long countProductsByBrandName(String productBrandName) {

		return productRepository.countByProductBrandName(productBrandName);
	}
}

