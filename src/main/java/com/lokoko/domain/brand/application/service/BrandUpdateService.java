package com.lokoko.domain.brand.application.service;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandMyPageUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandProfileImageRequest;
import com.lokoko.domain.brand.api.dto.response.BrandProfileImageResponse;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.productReview.exception.ErrorMessage;
import com.lokoko.domain.productReview.exception.InvalidMediaTypeException;
import com.lokoko.global.common.entity.MediaFile;
import com.lokoko.global.common.service.S3Service;
import com.lokoko.global.utils.S3UrlParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandUpdateService {

    private final S3Service s3Service;

    @Transactional
    public void updateBrandInfo(Brand brand, BrandInfoUpdateRequest request) {
        brand.assignBrandName(request.brandName());
        brand.assignManagerName(request.managerName());
        brand.assignManagerPosition(request.managerPosition());
        brand.assignPhoneNumber("+82" + request.phoneNumber());
        brand.assignRoadAddress(request.roadAddress());
        brand.assignAddressDetail(request.addressDetail());
    }

    @Transactional
    public BrandProfileImageResponse createBrandProfilePresignedUrl(Brand brand, BrandProfileImageRequest request) {
        String mediaType = request.mediaType();
        if (mediaType == null || mediaType.isBlank() || !mediaType.startsWith("image/")) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }
        String presignedUrl = s3Service.generatePresignedUrl(mediaType).presignedUrl();

        return new BrandProfileImageResponse(presignedUrl);
    }

    @Transactional
    public void updateBrandMyPage(Brand brand, BrandMyPageUpdateRequest request) {
        if (request.profileImageUrl() != null) {
            MediaFile mediaFile = S3UrlParser.parsePresignedUrl(request.profileImageUrl());
            brand.getUser().updateProfileImage(mediaFile.getFileUrl());
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