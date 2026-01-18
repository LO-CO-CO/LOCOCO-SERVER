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

    @Transactional
    public List<String> createPresignedUrlForProductImages(ProductImagePresignedUrlRequest request) {
        List<String> mediaTypes = request.mediaType();

        if (mediaTypes == null || mediaTypes.isEmpty()) {
            throw new ProductImageCountException();
        }

        if (mediaTypes.size() > MAX_IMAGE_COUNT) {
            throw new ProductImageCountException();
        }

        boolean allImage = mediaTypes.stream()
                .allMatch(type -> type.startsWith("image/"));

        if (!allImage) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }

        return mediaTypes.stream()
                .map(s3Service::generatePresignedUrl)
                .map(PresignedUrlResponse::presignedUrl)
                .toList();
    }

    @Transactional
    public AdminProductCreateResponse createProduct(AdminProductCreateRequest request) {

        ProductBrand productBrand = productBrandRepository.findById(request.productBrandId())
                .orElseThrow(ProductBrandNotFoundException::new);

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

        List<ProductImageRequest> images = request.images();
        validateImages(images);

        String mainUrl = images.get(0).url();

        List<ProductImage> productImages = images.stream()
                .map(img -> ProductImage.builder()
                        .product(savedProduct)
                        .url(img.url())
                        .isMain(img.url().equals(mainUrl))
                        .build())
                .collect(Collectors.toList());

        productImageRepository.saveAll(productImages);

        return AdminProductCreateResponse.builder()
                .productId(savedProduct.getId())
                .build();
    }

    private void validateImages(List<ProductImageRequest> images) {
        if (images == null || images.isEmpty()) {
            throw new ProductImageCountException();
        }
        if (images.size() > MAX_IMAGE_COUNT) {
            throw new ProductImageCountException();
        }
    }
}
