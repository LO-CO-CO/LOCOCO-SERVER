package com.lokoko.domain.productBrand.application.service;

import com.lokoko.domain.productBrand.domain.entity.ProductBrand;
import com.lokoko.domain.productBrand.domain.repository.ProductBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductBrandGetService {

    private final ProductBrandRepository productBrandRepository;

    public List<ProductBrand> getBrandNames(String startsWith) {

        Sort sort = Sort.by(Sort.Direction.ASC);

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
}

