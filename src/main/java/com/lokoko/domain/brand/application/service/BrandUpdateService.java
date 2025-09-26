package com.lokoko.domain.brand.application.service;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandMyPageUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandProfileImageRequest;
import com.lokoko.domain.brand.api.dto.response.BrandProfileImageResponse;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.media.application.service.S3Service;
import com.lokoko.domain.media.domain.MediaFile;
import com.lokoko.domain.productReview.exception.ErrorMessage;
import com.lokoko.domain.productReview.exception.InvalidMediaTypeException;
import com.lokoko.global.utils.S3UrlParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandUpdateService {

    private final S3Service s3Service;

    /**
     * 브랜드 기본 정보를 업데이트하는 메서드 - 브랜드명, 담당자명, 직책, 연락처, 주소 정보
     *
     * @param brand   업데이트할 브랜드 엔티티
     * @param request 브랜드 기본 정보 업데이트 요청 DTO
     */
    @Transactional
    public void updateBrandInfo(Brand brand, BrandInfoUpdateRequest request) {
        brand.assignBrandName(request.brandName());
        brand.assignManagerName(request.managerName());
        brand.assignManagerPosition(request.managerPosition());
        brand.assignPhoneNumber("+82" + request.phoneNumber());
        brand.assignRoadAddress(request.roadAddress());
        brand.assignAddressDetail(request.addressDetail());
    }

    /**
     * 브랜드 프로필 이미지 업로드를 위한 S3 프리사인드 URL을 생성하는 메서드 - mediaType 검증 후 URL을 반환
     *
     * @param brand   대상 브랜드 엔티티
     * @param request 프리사인드 URL 생성 요청 DTO (mediaType 포함)
     * @return {@link BrandProfileImageResponse} 생성된 프리사인드 URL 응답
     * @throws InvalidMediaTypeException mediaType가 null, 공백, 혹은 "image/"로 시작하지 않을 경우 예외
     */
    @Transactional
    public BrandProfileImageResponse createBrandProfilePresignedUrl(Brand brand, BrandProfileImageRequest request) {
        String mediaType = request.mediaType();
        if (mediaType == null || mediaType.isBlank() || !mediaType.startsWith("image/")) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }
        String presignedUrl = s3Service.generatePresignedUrl(mediaType).presignedUrl();

        return new BrandProfileImageResponse(presignedUrl);
    }

    /**
     * 브랜드 마이페이지 정보를 업데이트하는 메서드 - 프로필 이미지, 브랜드명, 담당자명, 연락처, 주소 등의 정보를 선택적 업데이트
     *
     * @param brand   업데이트할 브랜드 엔티티
     * @param request 브랜드 마이페이지 업데이트 요청 DTO
     */
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