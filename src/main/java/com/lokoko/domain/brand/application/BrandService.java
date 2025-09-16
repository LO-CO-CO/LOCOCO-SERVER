package com.lokoko.domain.brand.application;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandMyPageUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandProfileImageRequest;
import com.lokoko.domain.brand.api.dto.response.BrandMyPageResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileImageResponse;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.brand.domain.repository.BrandRepository;
import com.lokoko.domain.brand.exception.BrandNotFoundException;
import com.lokoko.domain.productReview.exception.ErrorMessage;
import com.lokoko.domain.productReview.exception.InvalidMediaTypeException;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.global.common.entity.MediaFile;
import com.lokoko.global.common.service.S3Service;
import com.lokoko.global.utils.S3UrlParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lokoko.global.utils.AllowedMediaType.ALLOWED_MEDIA_TYPES;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final S3Service s3Service;

    @Transactional
    public void updateBrandInfo(Long userId, BrandInfoUpdateRequest request) {
        Brand brand = brandRepository.findById(userId)
                .orElseThrow(BrandNotFoundException::new);

        brand.assignBrandName(request.brandName());
        brand.assignManagerName(request.managerName());
        brand.assignManagerPosition(request.managerPosition());
        brand.assignPhoneNumber("+82" + request.phoneNumber());
        brand.assignRoadAddress(request.roadAddress());
        brand.assignAddressDetail(request.addressDetail());

    }

    public BrandProfileImageResponse createBrandProfilePresignedUrl(Long brandId, BrandProfileImageRequest request) {

        brandRepository.findById(brandId).orElseThrow(BrandNotFoundException::new);

        String mediaType = request.mediaType();
        if (!mediaType.startsWith("image/")) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }

        String presignedUrl = s3Service.generatePresignedUrl(mediaType).presignedUrl();

        return new BrandProfileImageResponse(presignedUrl);
    }

    public BrandMyPageResponse getBrandMyPage(Long brandId) {
        Brand brand = brandRepository.findBrandWithUserById(brandId)
                .orElseThrow(BrandNotFoundException::new);

        return BrandMyPageResponse.from(brand, brand.getUser());
    }

    @Transactional
    public void updateBrandMyPage(Long brandId, BrandMyPageUpdateRequest request) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(BrandNotFoundException::new);

        if (request.profileImageUrl() != null) {
            MediaFile mediaFile = S3UrlParser.parsePresignedUrl(request.profileImageUrl());
            brand.getUser().updateProfileImage(mediaFile.toString());
        }
        if (request.brandName() != null) {
            brand.assignBrandName(request.brandName());
        }
        if (request.managerName() != null) {
            brand.assignManagerName(request.managerName());
        }
        if (request.phoneNumber() != null) {
            brand.assignPhoneNumber(request.phoneNumber());
        }
        if (request.roadAddress() != null) {
            brand.assignRoadAddress(request.roadAddress());
        }
        if (request.addressDetail() != null) {
            brand.assignAddressDetail(request.addressDetail());
        }
    }


}