package com.lokoko.domain.brand.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEFAULTS;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lokoko.domain.brand.api.dto.response.CreatorPerformanceResponse;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ContentStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.creator.api.dto.response.CreatorInfo;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.socialclip.application.service.SocialClipGetService;
import com.lokoko.domain.media.socialclip.domain.SocialClip;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.common.response.PageableResponse;
import com.lokoko.global.config.BetaFeatureConfig;

@DisplayName("Brand creator performance QueryService 테스트")
class BrandCreatorPerformanceQueryServiceTest {

	private static final String QUERY_SERVICE_CLASS_NAME =
		"com.lokoko.domain.brand.application.service.BrandCreatorPerformanceQueryService";

	private CreatorCampaignGetService creatorCampaignGetService;

	private CampaignReviewGetService campaignReviewGetService;

	private SocialClipGetService socialClipGetService;

	private BetaFeatureConfig betaFeatureConfig;

	private Object queryService;
	private Long campaignId;
	private Campaign campaign;
	private ContentType firstContentType;
	private ParticipationStatus approvedParticipationStatus;

	@BeforeEach
	void setUp() {
		creatorCampaignGetService = mock(CreatorCampaignGetService.class);
		campaignReviewGetService = mock(CampaignReviewGetService.class);
		socialClipGetService = mock(SocialClipGetService.class);
		betaFeatureConfig = mock(BetaFeatureConfig.class);

		Class<?> queryServiceClass = loadQueryServiceClass();
		Assumptions.assumeTrue(queryServiceClass != null, QUERY_SERVICE_CLASS_NAME + " is not present yet");

		queryService = instantiateQueryService(queryServiceClass);
		campaignId = 10L;
		campaign = mock(Campaign.class, RETURNS_DEEP_STUBS);
		firstContentType = ContentType.values()[0];
		approvedParticipationStatus = Arrays.stream(ParticipationStatus.values())
			.filter(participationStatus -> participationStatus != ParticipationStatus.REJECTED)
			.findFirst()
			.orElseThrow();

		given(campaign.getId()).willReturn(campaignId);
		given(campaign.getTitle()).willReturn("브랜드 릴스 캠페인");
		given(campaign.getFirstContentPlatform()).willReturn(firstContentType);
		given(campaign.getSecondContentPlatform()).willReturn(null);
	}

	@Test
	@DisplayName("getCreatorPerformances() : 1차/2차 리뷰가 함께 있으면 2차 리뷰를 우선 반영한다")
	void getCreatorPerformances_prioritizesSecondReview() {
		Creator creator = createCreator(11L, "홍길동", "creator-one", "https://cdn.test/creator-one.jpg");
		CreatorCampaign creatorCampaign =
			createCreatorCampaign(101L, creator, approvedParticipationStatus, true);

		CampaignReview firstReview =
			createCampaignReview(1001L, ReviewRound.FIRST, ReviewStatus.SUBMITTED, firstContentType);
		CampaignReview secondReview =
			createCampaignReview(1002L, ReviewRound.SECOND, ReviewStatus.RESUBMITTED, firstContentType);

		SocialClip socialClip = mock(SocialClip.class);
		Instant uploadedAt = Instant.parse("2026-03-01T12:00:00Z");

		given(secondReview.getPostUrl()).willReturn("https://instagram.com/p/final");
		given(creatorCampaignGetService.findAllByCampaign(campaign)).willReturn(List.of(creatorCampaign));
		given(campaignReviewGetService.findAllByCreatorCampaignId(101L))
			.willReturn(List.of(firstReview, secondReview));
		given(betaFeatureConfig.isSimplifiedReviewFlow()).willReturn(false);

		given(socialClipGetService.findByCampaignReview(secondReview)).willReturn(Optional.of(socialClip));
		given(socialClip.getPlays()).willReturn(1200L);
		given(socialClip.getLikes()).willReturn(300L);
		given(socialClip.getComments()).willReturn(45L);
		given(socialClip.getShares()).willReturn(12L);
		given(socialClip.getUploadedAt()).willReturn(uploadedAt);

		CreatorPerformanceResponse result = invokeGetCreatorPerformances(0, 10);

		CreatorPerformanceResponse expected = CreatorPerformanceResponse.builder()
			.campaignId(campaignId)
			.campaignTitle("브랜드 릴스 캠페인")
			.firstContentPlatform(firstContentType)
			.secondContentPlatform(null)
			.creators(List.of(
				CreatorPerformanceResponse.CreatorReviewPerformance.builder()
					.creator(createCreatorInfo(11L, "홍길동", "creator-one", "https://cdn.test/creator-one.jpg"))
					.reviews(List.of(
						CreatorPerformanceResponse.ReviewPerformance.builder()
							.campaignReviewId(1002L)
							.reviewRound(ReviewRound.SECOND)
							.reviewStatus(ContentStatus.FINAL_UPLOADED)
							.postUrl("https://instagram.com/p/final")
							.contents(CreatorPerformanceResponse.ContentMetrics.builder()
								.contentType(firstContentType)
								.viewCount(1200L)
								.likeCount(300L)
								.commentCount(45L)
								.shareCount(12L)
								.build())
							.uploadedAt(uploadedAt)
							.build()
					))
					.build()
			))
			.pageableResponse(PageableResponse.of(0, 10, 1, true, 1L))
			.build();

		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	@Test
	@DisplayName("getCreatorPerformances() : 리뷰가 없고 배송지 입력이 완료되었으면 진행중 상태를 반환한다")
	void getCreatorPerformances_returnsInProgress_whenAddressConfirmedAndNoReview() {
		Creator creator = createCreator(21L, "김로코", "creator-two", "https://cdn.test/creator-two.jpg");
		CreatorCampaign creatorCampaign =
			createCreatorCampaign(201L, creator, approvedParticipationStatus, true);

		given(creatorCampaignGetService.findAllByCampaign(campaign)).willReturn(List.of(creatorCampaign));
		given(campaignReviewGetService.findAllByCreatorCampaignId(201L)).willReturn(List.of());
		given(betaFeatureConfig.isSimplifiedReviewFlow()).willReturn(false);

		CreatorPerformanceResponse result = invokeGetCreatorPerformances(0, 10);

		CreatorPerformanceResponse expected = CreatorPerformanceResponse.builder()
			.campaignId(campaignId)
			.campaignTitle("브랜드 릴스 캠페인")
			.firstContentPlatform(firstContentType)
			.secondContentPlatform(null)
			.creators(List.of(
				CreatorPerformanceResponse.CreatorReviewPerformance.builder()
					.creator(createCreatorInfo(21L, "김로코", "creator-two", "https://cdn.test/creator-two.jpg"))
					.reviews(List.of(
						CreatorPerformanceResponse.ReviewPerformance.builder()
							.reviewRound(ReviewRound.FIRST)
							.reviewStatus(ContentStatus.IN_PROGRESS)
							.contents(CreatorPerformanceResponse.ContentMetrics.builder()
								.contentType(firstContentType)
								.build())
							.build()
					))
					.build()
			))
			.pageableResponse(PageableResponse.of(0, 10, 1, true, 1L))
			.build();

		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	@Test
	@DisplayName("getCreatorPerformances() : 리뷰가 없고 배송지 입력이 없으면 미제출 상태를 반환한다")
	void getCreatorPerformances_returnsNotSubmitted_whenAddressNotConfirmedAndNoReview() {
		Creator creator = createCreator(31L, "박지원", "creator-three", "https://cdn.test/creator-three.jpg");
		CreatorCampaign creatorCampaign =
			createCreatorCampaign(301L, creator, approvedParticipationStatus, false);

		given(creatorCampaignGetService.findAllByCampaign(campaign)).willReturn(List.of(creatorCampaign));
		given(campaignReviewGetService.findAllByCreatorCampaignId(301L)).willReturn(List.of());
		given(betaFeatureConfig.isSimplifiedReviewFlow()).willReturn(false);

		CreatorPerformanceResponse result = invokeGetCreatorPerformances(0, 10);

		CreatorPerformanceResponse expected = CreatorPerformanceResponse.builder()
			.campaignId(campaignId)
			.campaignTitle("브랜드 릴스 캠페인")
			.firstContentPlatform(firstContentType)
			.secondContentPlatform(null)
			.creators(List.of(
				CreatorPerformanceResponse.CreatorReviewPerformance.builder()
					.creator(createCreatorInfo(31L, "박지원", "creator-three", "https://cdn.test/creator-three.jpg"))
					.reviews(List.of(
						CreatorPerformanceResponse.ReviewPerformance.builder()
							.reviewRound(ReviewRound.FIRST)
							.reviewStatus(ContentStatus.NOT_SUBMITTED)
							.contents(CreatorPerformanceResponse.ContentMetrics.builder()
								.contentType(firstContentType)
								.build())
							.build()
					))
					.build()
			))
			.pageableResponse(PageableResponse.of(0, 10, 1, true, 1L))
			.build();

		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	@Test
	@DisplayName("getCreatorPerformances() : 베타 모드에서는 1차 SUBMITTED 리뷰를 최종 업로드로 처리하고 postUrl을 포함한다")
	void getCreatorPerformances_betaMode_returnsFinalUploadedForFirstReview() {
		Creator creator = createCreator(41L, "최지훈", "creator-four", "https://cdn.test/creator-four.jpg");
		CreatorCampaign creatorCampaign =
			createCreatorCampaign(401L, creator, approvedParticipationStatus, true);

		CampaignReview firstReview =
			createCampaignReview(4001L, ReviewRound.FIRST, ReviewStatus.SUBMITTED, firstContentType);

		LocalDateTime createdAt = LocalDateTime.of(2026, 3, 1, 12, 0);
		Instant expectedUploadedAt = createdAt.toInstant(ZoneOffset.ofHours(9));

		given(betaFeatureConfig.isSimplifiedReviewFlow()).willReturn(true);
		given(betaFeatureConfig.isFirstReviewUrlEnabled()).willReturn(true);

		given(firstReview.getPostUrl()).willReturn("https://instagram.com/p/beta-first");
		given(firstReview.getCreatedAt()).willReturn(createdAt);

		given(creatorCampaignGetService.findAllByCampaign(campaign)).willReturn(List.of(creatorCampaign));
		given(campaignReviewGetService.findAllByCreatorCampaignId(401L))
			.willReturn(List.of(firstReview));

		CreatorPerformanceResponse result = invokeGetCreatorPerformances(0, 10);

		CreatorPerformanceResponse expected = CreatorPerformanceResponse.builder()
			.campaignId(campaignId)
			.campaignTitle("브랜드 릴스 캠페인")
			.firstContentPlatform(firstContentType)
			.secondContentPlatform(null)
			.creators(List.of(
				CreatorPerformanceResponse.CreatorReviewPerformance.builder()
					.creator(createCreatorInfo(41L, "최지훈", "creator-four", "https://cdn.test/creator-four.jpg"))
					.reviews(List.of(
						CreatorPerformanceResponse.ReviewPerformance.builder()
							.campaignReviewId(4001L)
							.reviewRound(ReviewRound.FIRST)
							.reviewStatus(ContentStatus.FINAL_UPLOADED)
							.postUrl("https://instagram.com/p/beta-first")
							.contents(CreatorPerformanceResponse.ContentMetrics.builder()
								.contentType(firstContentType)
								.build())
							.uploadedAt(expectedUploadedAt)
							.build()
					))
					.build()
			))
			.pageableResponse(PageableResponse.of(0, 10, 1, true, 1L))
			.build();

		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	@Test
	@DisplayName("getCreatorPerformances() : REJECTED 지원자는 결과에서 제외한다")
	void getCreatorPerformances_excludesRejectedCreatorCampaign() {
		Creator creator = createCreator(51L, "서로코", "creator-five", "https://cdn.test/creator-five.jpg");
		CreatorCampaign rejectedCreatorCampaign =
			createCreatorCampaign(501L, creator, ParticipationStatus.REJECTED, false);

		given(creatorCampaignGetService.findAllByCampaign(campaign))
			.willReturn(List.of(rejectedCreatorCampaign));
		given(betaFeatureConfig.isSimplifiedReviewFlow()).willReturn(false);

		CreatorPerformanceResponse result = invokeGetCreatorPerformances(0, 10);

		CreatorPerformanceResponse expected = CreatorPerformanceResponse.builder()
			.campaignId(campaignId)
			.campaignTitle("브랜드 릴스 캠페인")
			.firstContentPlatform(firstContentType)
			.secondContentPlatform(null)
			.creators(List.of())
			.pageableResponse(PageableResponse.of(0, 10, 0, true, 0L))
			.build();

		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	private Class<?> loadQueryServiceClass() {
		try {
			return Class.forName(QUERY_SERVICE_CLASS_NAME);
		} catch (ClassNotFoundException exception) {
			return null;
		}
	}

	private Object instantiateQueryService(Class<?> queryServiceClass) {
		try {
			Constructor<?> constructor = Arrays.stream(queryServiceClass.getDeclaredConstructors())
				.max((left, right) -> Integer.compare(left.getParameterCount(), right.getParameterCount()))
				.orElseThrow();

			constructor.setAccessible(true);

			Object[] arguments = Arrays.stream(constructor.getParameterTypes())
				.map(this::resolveConstructorArgument)
				.toArray();

			return constructor.newInstance(arguments);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException("Failed to instantiate " + QUERY_SERVICE_CLASS_NAME, exception);
		}
	}

	@SuppressWarnings("unchecked")
	private Object resolveConstructorArgument(Class<?> parameterType) {
		if (parameterType == CreatorCampaignGetService.class) {
			return creatorCampaignGetService;
		}
		if (parameterType == CampaignReviewGetService.class) {
			return campaignReviewGetService;
		}
		if (parameterType == SocialClipGetService.class) {
			return socialClipGetService;
		}
		if (parameterType == BetaFeatureConfig.class) {
			return betaFeatureConfig;
		}
		return mock((Class<Object>)parameterType, RETURNS_DEFAULTS);
	}

	private CreatorPerformanceResponse invokeGetCreatorPerformances(int page, int size) {
		try {
			Method method = Arrays.stream(queryService.getClass().getMethods())
				.filter(candidate -> candidate.getName().equals("getCreatorPerformances"))
				.findFirst()
				.orElseThrow(() -> new NoSuchMethodException("getCreatorPerformances"));

			method.setAccessible(true);

			return (CreatorPerformanceResponse)method.invoke(
				queryService,
				resolveMethodArguments(method, page, size)
			);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException("Failed to invoke getCreatorPerformances on query service", exception);
		}
	}

	private Object[] resolveMethodArguments(Method method, int page, int size) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		int totalIntParameters = countParameters(parameterTypes, Integer.class, int.class);
		int intIndex = 0;
		List<Object> arguments = new ArrayList<>(parameterTypes.length);

		for (Class<?> parameterType : parameterTypes) {
			if (parameterType == Campaign.class) {
				arguments.add(campaign);
				continue;
			}
			if (parameterType == Long.class || parameterType == long.class) {
				arguments.add(campaignId);
				continue;
			}
			if (parameterType == Integer.class || parameterType == int.class) {
				if (totalIntParameters == 1) {
					arguments.add(page);
				} else {
					arguments.add(intIndex++ == 0 ? page : size);
				}
				continue;
			}
			throw new IllegalStateException("Unsupported getCreatorPerformances parameter: " + parameterType.getName());
		}

		return arguments.toArray();
	}

	private int countParameters(Class<?>[] parameterTypes, Class<?> wrapperType, Class<?> primitiveType) {
		int count = 0;
		for (Class<?> parameterType : parameterTypes) {
			if (parameterType == wrapperType || parameterType == primitiveType) {
				count++;
			}
		}
		return count;
	}

	private Creator createCreator(
		Long creatorId,
		String creatorFullName,
		String creatorNickname,
		String profileImageUrl
	) {
		Creator creator = mock(Creator.class, RETURNS_DEEP_STUBS);

		given(creator.getId()).willReturn(creatorId);
		given(creator.getCreatorName()).willReturn(creatorNickname);
		given(creator.getUser().getName()).willReturn(creatorFullName);
		given(creator.getUser().getProfileImageUrl()).willReturn(profileImageUrl);

		return creator;
	}

	private CreatorCampaign createCreatorCampaign(
		Long creatorCampaignId,
		Creator creator,
		ParticipationStatus participationStatus,
		Boolean addressConfirmed
	) {
		CreatorCampaign creatorCampaign = mock(CreatorCampaign.class);

		given(creatorCampaign.getId()).willReturn(creatorCampaignId);
		given(creatorCampaign.getCreator()).willReturn(creator);
		given(creatorCampaign.getStatus()).willReturn(participationStatus);
		given(creatorCampaign.getAddressConfirmed()).willReturn(addressConfirmed);

		return creatorCampaign;
	}

	private CampaignReview createCampaignReview(
		Long campaignReviewId,
		ReviewRound reviewRound,
		ReviewStatus reviewStatus,
		ContentType contentType
	) {
		CampaignReview campaignReview = mock(CampaignReview.class);

		given(campaignReview.getId()).willReturn(campaignReviewId);
		given(campaignReview.getReviewRound()).willReturn(reviewRound);
		given(campaignReview.getStatus()).willReturn(reviewStatus);
		given(campaignReview.getContentType()).willReturn(contentType);

		return campaignReview;
	}

	private CreatorInfo createCreatorInfo(
		Long creatorId,
		String creatorFullName,
		String creatorNickname,
		String profileImageUrl
	) {
		return CreatorInfo.builder()
			.creatorId(creatorId)
			.creatorFullName(creatorFullName)
			.creatorNickname(creatorNickname)
			.profileImageUrl(profileImageUrl)
			.build();
	}
}
