package com.lokoko.domain.brand;

import static org.mockito.Answers.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lokoko.domain.brand.application.service.BrandGetService;
import com.lokoko.domain.brand.application.service.BrandUpdateService;
import com.lokoko.domain.brand.application.usecase.BrandUsecase;
import com.lokoko.domain.campaign.application.mapper.CampaignMapper;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaignReview.application.mapper.CampaignReviewMapper;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewStatusManager;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.media.socialclip.application.service.SocialClipGetService;
import com.lokoko.global.config.BetaFeatureConfig;

@ExtendWith(MockitoExtension.class)
abstract class BrandUsecaseTestSupport {

	private static final String BRAND_CREATOR_PERFORMANCE_QUERY_SERVICE =
		"com.lokoko.domain.brand.application.service.BrandCreatorPerformanceQueryService";

	@Mock
	protected BrandGetService brandGetService;

	@Mock
	protected CampaignGetService campaignGetService;

	@Mock
	protected CreatorCampaignGetService creatorCampaignGetService;

	@Mock
	protected CampaignReviewGetService campaignReviewGetService;

	@Mock
	protected SocialClipGetService socialClipGetService;

	@Mock
	protected BrandUpdateService brandUpdateService;

	@Mock
	protected CampaignReviewStatusManager campaignReviewStatusManager;

	@Mock
	protected BetaFeatureConfig betaFeatureConfig;

	@Mock
	protected CampaignMapper campaignMapper;

	@Mock
	protected CampaignReviewMapper campaignReviewMapper;

	protected BrandUsecase brandUsecase;

	private final Map<String, Object> dynamicMocks = new HashMap<>();
	private final Map<String, Object> dynamicMethodReturns = new HashMap<>();

	@BeforeEach
	void initializeBrandUsecase() {
		brandUsecase = instantiateBrandUsecase();
	}

	protected boolean hasDynamicMock(String className) {
		return dynamicMocks.containsKey(className);
	}

	protected void stubDynamicMethodReturn(String className, String methodName, Object returnValue) {
		dynamicMethodReturns.put(dynamicMethodKey(className, methodName), returnValue);
	}

	private BrandUsecase instantiateBrandUsecase() {
		try {
			Constructor<?> constructor = Arrays.stream(BrandUsecase.class.getDeclaredConstructors())
				.max((left, right) -> Integer.compare(left.getParameterCount(), right.getParameterCount()))
				.orElseThrow();

			constructor.setAccessible(true);

			Object[] arguments = Arrays.stream(constructor.getParameterTypes())
				.map(this::resolveConstructorArgument)
				.toArray();

			return (BrandUsecase)constructor.newInstance(arguments);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException("Failed to instantiate BrandUsecase for tests", exception);
		}
	}

	@SuppressWarnings("unchecked")
	private Object resolveConstructorArgument(Class<?> parameterType) {
		if (parameterType == BrandGetService.class) {
			return brandGetService;
		}
		if (parameterType == CampaignGetService.class) {
			return campaignGetService;
		}
		if (parameterType == CreatorCampaignGetService.class) {
			return creatorCampaignGetService;
		}
		if (parameterType == CampaignReviewGetService.class) {
			return campaignReviewGetService;
		}
		if (parameterType == SocialClipGetService.class) {
			return socialClipGetService;
		}
		if (parameterType == BrandUpdateService.class) {
			return brandUpdateService;
		}
		if (parameterType == BetaFeatureConfig.class) {
			return betaFeatureConfig;
		}
		if (parameterType == CampaignMapper.class) {
			return campaignMapper;
		}
		if (parameterType == CampaignReviewMapper.class) {
			return campaignReviewMapper;
		}
		if (parameterType.getName().equals(BRAND_CREATOR_PERFORMANCE_QUERY_SERVICE)) {
			return dynamicMocks.computeIfAbsent(
				parameterType.getName(),
				key -> mock((Class<Object>)parameterType, invocation -> {
					String methodKey = dynamicMethodKey(parameterType.getName(), invocation.getMethod().getName());
					if (dynamicMethodReturns.containsKey(methodKey)) {
						return dynamicMethodReturns.get(methodKey);
					}
					return RETURNS_DEFAULTS.answer(invocation);
				})
			);
		}
		return mock((Class<Object>)parameterType, RETURNS_DEFAULTS);
	}

	private String dynamicMethodKey(String className, String methodName) {
		return className + "#" + methodName;
	}
}
