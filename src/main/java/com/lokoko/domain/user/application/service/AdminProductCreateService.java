package com.lokoko.domain.user.application.service;


import com.lokoko.domain.media.api.dto.request.ProductImagePresignedUrlRequest;
import com.lokoko.domain.media.api.dto.request.ProductImageRequest;
import com.lokoko.domain.media.api.dto.response.PresignedUrlResponse;
import com.lokoko.domain.media.application.service.S3Service;
import com.lokoko.domain.media.image.domain.entity.ProductImage;
import com.lokoko.domain.media.image.domain.repository.ProductImageRepository;
import com.lokoko.domain.media.image.exception.ProductImageCountException;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.productBrand.domain.entity.ProductBrand;
import com.lokoko.domain.productBrand.domain.repository.ProductBrandRepository;
import com.lokoko.domain.productBrand.exception.ProductBrandNotFoundException;
import com.lokoko.domain.productReview.exception.ErrorMessage;
import com.lokoko.domain.productReview.exception.InvalidMediaTypeException;
import com.lokoko.domain.user.api.dto.request.AdminProductCreateRequest;
import com.lokoko.domain.user.api.dto.response.AdminProductCreateResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductCreateService {

    private final ProductRepository productRepository;
    private final ProductBrandRepository productBrandRepository;
    private final ProductImageRepository productImageRepository;

    private final S3Service s3Service;

    private static final int MAX_IMAGE_COUNT = 5;

    public List<String> createPresignedUrlForProductImages(ProductImagePresignedUrlRequest request) {
        List<String> mediaTypes = request.mediaType();

        int imageCount = mediaTypes == null ? 0 : mediaTypes.size();

        validateImageCount(imageCount);
        validateImageMediaType(mediaTypes);

        return mediaTypes.stream()
                .map(s3Service::generatePresignedUrl)
                .map(PresignedUrlResponse::presignedUrl)
                .toList();
    }

    @Transactional
    public AdminProductCreateResponse createProduct(AdminProductCreateRequest request) {

        ProductBrand productBrand = productBrandRepository.findById(request.productBrandId())
                .orElseThrow(ProductBrandNotFoundException::new);

        List<ProductImageRequest> images = request.images();
        int imageCount = images == null ? 0 : images.size();
        validateImageCount(imageCount);

        Product product = Product.builder()
                .productBrand(productBrand)
                .productName(request.productName())
                .normalPrice(request.normalPrice())
                .unit(request.unit())
                .productCategory(request.category())
                .manufacturedAt(request.manufacturedAt())
                .productDetail(request.productDetail())
                .ingredients(request.ingredients())
                .build();

        Product savedProduct = productRepository.save(product);

        List<ProductImage> productImages = images.stream()
                .map(img -> ProductImage.builder()
                        .product(savedProduct)
                        .url(img.url())
                        .isMain(img.displayOrder() == 0)
                        .build())
                .collect(Collectors.toList());

        productImageRepository.saveAll(productImages);

        return AdminProductCreateResponse.builder()
                .productId(savedProduct.getId())
                .build();
    }

    private void validateImageCount(int imageCount) {
        if (imageCount <= 0) {
            throw new ProductImageCountException();
        }
        if (imageCount > MAX_IMAGE_COUNT) {
            throw new ProductImageCountException();
        }
    }

    private void validateImageMediaType(List<String> mediaTypes) {
        boolean hasInvalidMediaType = mediaTypes.stream()
                .anyMatch(type -> !type.startsWith("image/"));

        if (hasInvalidMediaType) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }
    }
}
