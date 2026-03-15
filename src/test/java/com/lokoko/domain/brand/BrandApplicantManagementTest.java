package com.lokoko.domain.brand;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.lokoko.domain.brand.api.dto.response.CreatorPerformanceResponse;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;

@DisplayName("Brand 지원자/성과 관리 Usecase 테스트")
@MockitoSettings(strictness = Strictness.LENIENT)
class BrandApplicantManagementTest extends BrandUsecaseTestSupport {

	private static final String BRAND_CREATOR_PERFORMANCE_QUERY_SERVICE =
		"com.lokoko.domain.brand.application.service.BrandCreatorPerformanceQueryService";

	private Long brandId;
	private Long campaignId;
	private Brand brand;
	private Campaign campaign;
	private ContentType firstContentType;

	@BeforeEach
	void setUp() {
		brandId = 1L;
		campaignId = 10L;
		brand = mock(Brand.class, RETURNS_DEEP_STUBS);
		campaign = mock(Campaign.class, RETURNS_DEEP_STUBS);
		firstContentType = ContentType.values()[0];

		given(brandGetService.getBrandById(brandId)).willReturn(brand);
		given(brand.getId()).willReturn(brandId);
		given(campaignGetService.findByCampaignId(campaignId)).willReturn(campaign);
		given(campaign.getId()).willReturn(campaignId);
		given(campaign.getBrand().getId()).willReturn(brandId);
		given(campaign.getTitle()).willReturn("브랜드 릴스 캠페인");
		given(campaign.getFirstContentPlatform()).willReturn(firstContentType);
		given(campaign.getSecondContentPlatform()).willReturn(null);

		given(betaFeatureConfig.isSimplifiedReviewFlow()).willReturn(false);
		given(betaFeatureConfig.isFirstReviewUrlEnabled()).willReturn(false);
	}

	@Nested
	@DisplayName("브랜드 권한 검증")
	class OwnershipValidation {

		@Test
		@DisplayName("getCreatorPerformances() : 다른 브랜드의 캠페인이면 예외를 던진다")
		void getCreatorPerformances_notOwner_throwsException() {
			// given
			given(campaign.getBrand().getId()).willReturn(999L);

			// when & then
			assertThatThrownBy(() -> brandUsecase.getCreatorPerformances(brandId, campaignId, 0, 10))
				.isInstanceOf(NotCampaignOwnershipException.class);
		}
	}

	@Nested
	@DisplayName("브랜드 성과 조회 위임")
	class CreatorPerformanceDelegation {

		@Test
		@DisplayName("getCreatorPerformances() : 리팩터링 후에는 권한 검증 뒤 QueryService 응답을 그대로 반환한다")
		void getCreatorPerformances_returnsQueryServiceResponse_whenQueryServiceExists() {
			Assumptions.assumeTrue(hasDynamicMock(BRAND_CREATOR_PERFORMANCE_QUERY_SERVICE));

			CreatorPerformanceResponse expected = mock(CreatorPerformanceResponse.class);
			stubDynamicMethodReturn(
				BRAND_CREATOR_PERFORMANCE_QUERY_SERVICE,
				"getCreatorPerformances",
				expected
			);

			CreatorPerformanceResponse result =
				brandUsecase.getCreatorPerformances(brandId, campaignId, 0, 10);

			assertThat(result).isSameAs(expected);
		}
	}
}
