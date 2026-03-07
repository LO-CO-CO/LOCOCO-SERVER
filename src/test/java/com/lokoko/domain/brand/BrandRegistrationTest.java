package com.lokoko.domain.brand;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Answers.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandProfileImageRequest;
import com.lokoko.domain.brand.api.dto.response.BrandProfileImageResponse;
import com.lokoko.domain.brand.domain.entity.Brand;

@DisplayName("Brand 회원가입 Usecase 테스트")
class BrandRegistrationTest extends BrandUsecaseTestSupport {

	private Long brandId;
	private Brand brand;
	private BrandProfileImageRequest brandProfileImageRequest;
	private BrandInfoUpdateRequest brandInfoUpdateRequest;
	private BrandProfileImageResponse brandProfileImageResponse;

	@BeforeEach
	void setUp() {
		brandId = 1L;
		brand = mock(Brand.class, RETURNS_DEEP_STUBS);
		brandProfileImageRequest = mock(BrandProfileImageRequest.class);
		brandInfoUpdateRequest = mock(BrandInfoUpdateRequest.class);
		brandProfileImageResponse = mock(BrandProfileImageResponse.class);
	}

	@Nested
	@DisplayName("브랜드 프로필 이미지 presigned url 발급")
	class CreateBrandProfilePresignedUrl {

		@Test
		@DisplayName("createBrandProfilePresignedUrl() : 브랜드를 조회한 뒤 presigned url 응답을 반환한다")
		void createBrandProfilePresignedUrl_success() {
			// given
			given(brandGetService.getBrandById(brandId)).willReturn(brand);
			given(brandUpdateService.createBrandProfilePresignedUrl(brand, brandProfileImageRequest))
				.willReturn(brandProfileImageResponse);

			// when
			BrandProfileImageResponse result =
				brandUsecase.createBrandProfilePresignedUrl(brandId, brandProfileImageRequest);

			// then
			assertThat(result).isSameAs(brandProfileImageResponse);
		}
	}

	@Nested
	@DisplayName("브랜드 회원가입 추가 정보 입력")
	class UpdateBrandInfo {

		@Test
		@DisplayName("updateBrandInfo() : 브랜드를 조회한 뒤 추가 정보를 수정한다")
		void updateBrandInfo_success() {
			// given
			given(brandGetService.getBrandById(brandId)).willReturn(brand);

			// when & then
			assertThatCode(() -> brandUsecase.updateBrandInfo(brandId, brandInfoUpdateRequest))
				.doesNotThrowAnyException();

			then(brandUpdateService).should().updateBrandInfo(brand, brandInfoUpdateRequest);
		}
	}
}