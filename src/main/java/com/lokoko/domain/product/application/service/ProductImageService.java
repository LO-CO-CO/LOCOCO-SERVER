package com.lokoko.domain.product.application.service;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.lokoko.domain.image.domain.entity.ProductImage;
import com.lokoko.domain.image.domain.repository.ProductImageRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    public Map<Long, String> mapMainImageUrlsByProductIds(List<Long> productIds) {
        List<ProductImage> images = productImageRepository.findByProductIdIn(productIds);
        return images.stream()
                .collect(groupingBy(
                        img -> img.getProduct().getId(),
                        collectingAndThen(toList(), list ->
                                list.stream()
                                        .filter(ProductImage::isMain)
                                        .findFirst()
                                        .orElse(list.get(0))
                                        .getUrl()
                        )
                ));
    }

    public Map<Long, List<String>> mapAllImageUrls(List<ProductImage> images) {
        return images.stream()
                .collect(groupingBy(
                        img -> img.getProduct().getId(),
                        mapping(ProductImage::getUrl, toList())
                ));
    }
}
