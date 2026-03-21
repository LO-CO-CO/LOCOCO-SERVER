package com.lokoko.domain.campaignReview.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.api.dto.request.FirstReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.request.SecondReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.response.CompletedReviewResponse;
import com.lokoko.domain.campaignReview.api.dto.response.ReviewUploadResponse;
import com.lokoko.domain.campaignReview.application.mapper.CampaignReviewMapper;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewReadService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewSaveService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewUpdateService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewValidationService;
import com.lokoko.domain.campaignReview.application.service.CreatorCampaignUpdateService;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creator.application.service.CreatorGetService;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.media.api.dto.request.MediaPresignedUrlRequest;
import com.lokoko.domain.media.api.dto.response.MediaPresignedUrlResponse;
import com.lokoko.domain.media.application.utils.MediaValidationUtil;
import com.lokoko.domain.media.socialclip.application.service.SocialClipSaveService;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.config.BetaFeatureConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignReviewUsecase {

	private final CreatorGetService creatorGetService;
	private final CampaignReviewGetService campaignReviewGetService;
	private final CampaignGetService campaignGetService;
	private final CreatorCampaignGetService creatorCampaignGetService;

	private final CampaignReviewSaveService campaignReviewSaveService;
	private final CreatorCampaignUpdateService creatorCampaignUpdateService;
	private final CampaignReviewUpdateService campaignReviewUpdateService;
	private final CampaignReviewReadService campaignReviewReadService;
	private final CampaignReviewValidationService campaignReviewValidationService;
	private final SocialClipSaveService socialClipSaveService;

	private final BetaFeatureConfig betaFeatureConfig;

	private final CampaignReviewMapper campaignReviewMapper;

	/**
	 * 1차 리뷰 업로드 - 타입은 Campaign.firstContentPlatform / secondContentPlatform 사용 - 두개 리뷰 컨텐츠를 입력 받아야하는 캠페인이면 2세트 모두 필수
	 * 아니면 첫세트만 허용 - 동일 타입의 FIRST가 이미 존재하면 409
	 */
	@Transactional
	public ReviewUploadResponse uploadFirst(Long userId, Long campaignId, FirstReviewUploadRequest request) {
		Creator creator = creatorGetService.findByUserId(userId);
		Campaign campaign = campaignGetService.findByCampaignId(campaignId);
		CreatorCampaign participation =
			creatorCampaignGetService.getByCampaignAndCreatorId(campaign, creator.getId());

		ContentType typeA = campaign.getFirstContentPlatform();
		ContentType typeB = campaign.getSecondContentPlatform();
		campaignReviewValidationService.validateTwoSetCombination(typeA, typeB);

		// A 세트(캠페인에 second가 없는 단일 타입 캠페인)
		// 베타 버전에서는 firstMediaUrls, firstCaptionWithHashtags 에 대한 검증을 진행하지 않는다.
		if (!betaFeatureConfig.isSimplifiedReviewFlow()) {
			campaignReviewValidationService.requireFirstSetPresent(request.firstMediaUrls(),
				request.firstCaptionWithHashtags());
		}
		if (request.firstMediaUrls() != null && !request.firstMediaUrls().isEmpty()) {
			MediaValidationUtil.validateTotalMediaCount(request.firstMediaUrls());
		}

		// B 세트(캠페인에 second가 있으면 필수, 없으면 금지)
		if (typeB != null) {
			// 베타 버전에서는 secondMediaUrls, secondCaptionWithHashtags 에 대한 검증을 진행하지 않는다.
			if (!betaFeatureConfig.isSimplifiedReviewFlow()) {
				campaignReviewValidationService.requireFirstSetPresent(
					request.secondMediaUrls(), request.secondCaptionWithHashtags());
			}
			if (request.secondMediaUrls() != null && !request.secondMediaUrls().isEmpty()) {
				MediaValidationUtil.validateTotalMediaCount(request.secondMediaUrls());
			}
		} else {
			campaignReviewValidationService.ensureSecondSetAbsentForFirstRound(
				request.secondMediaUrls(), request.secondCaptionWithHashtags());
		}

		// 미디어 합산 개수 제한
		campaignReviewValidationService.validateCombinedMediaLimit(
			request.firstMediaUrls(),
			(typeB != null) ? request.secondMediaUrls() : null
		);

		// 저장 A (베타 모드일 경우 postUrl 포함)
		CampaignReview firstA;
		if (betaFeatureConfig.isFirstReviewUrlEnabled() && request.firstPostUrl() != null) {
			firstA = campaignReviewMapper.toFirstReview(
				participation, typeA, request.firstCaptionWithHashtags(), request.firstPostUrl());
		} else {
			firstA = campaignReviewMapper.toFirstReview(
				participation, typeA, request.firstCaptionWithHashtags());
		}
		CampaignReview savedA = campaignReviewSaveService.saveReview(firstA);
		campaignReviewSaveService.saveMedia(savedA, request.firstMediaUrls());

		// 저장 B(옵션)
		if (typeB != null) {
			CampaignReview firstB;
			if (betaFeatureConfig.isFirstReviewUrlEnabled() && request.secondPostUrl() != null) {
				firstB = campaignReviewMapper.toFirstReview(
					participation, typeB, request.secondCaptionWithHashtags(), request.secondPostUrl());
			} else {
				firstB = campaignReviewMapper.toFirstReview(
					participation, typeB, request.secondCaptionWithHashtags());
			}
			CampaignReview savedB = campaignReviewSaveService.saveReview(firstB);
			campaignReviewSaveService.saveMedia(savedB, request.secondMediaUrls());
		}

		creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());
		return campaignReviewMapper.toUploadResponse(savedA);
	}

	/**
	 * 2차 리뷰 업로드 - 타입은 Campaign.firstContentPlatform / secondContentPlatform 사용
	 * <p> - 두 리뷰 컨텐츠를 모두 입력 받는 캠페인이면 2세트 모두 필수 (각각 postUrl 포함)
	 * <p> 아니면 첫 세트만 허용(두 번째 세트 전달 시 400)
	 * <p> - 각 타입별로: 1차 존재 + 동일 타입이라면 이미 업로드한 2차 리뷰가 없다는 조건이 충족해야 함
	 */
	@Transactional
	public ReviewUploadResponse uploadSecond(Long userId, Long campaignId, SecondReviewUploadRequest request) {
		Creator creator = creatorGetService.findByUserId(userId);
		Campaign campaign = campaignGetService.findByCampaignId(campaignId);
		CreatorCampaign participation =
			creatorCampaignGetService.getByCampaignAndCreatorId(campaign, creator.getId());

		ContentType typeA = campaign.getFirstContentPlatform();
		ContentType typeB = campaign.getSecondContentPlatform();
		campaignReviewValidationService.validateTwoSetCombination(typeA, typeB);

		// A 세트(필수: 미디어/캡션/postUrl)
		campaignReviewValidationService.requireSecondSetPresent(
			request.firstMediaUrls(), request.firstCaptionWithHashtags(), request.firstPostUrl());
		MediaValidationUtil.validateTotalMediaCount(request.firstMediaUrls());

		// B 세트(캠페인에 second가 있으면 필수, 없으면 금지)
		if (typeB != null) {
			campaignReviewValidationService.requireSecondSetPresent(
				request.secondMediaUrls(), request.secondCaptionWithHashtags(), request.secondPostUrl());
			MediaValidationUtil.validateTotalMediaCount(request.secondMediaUrls());
		} else {
			campaignReviewValidationService.ensureSecondSetAbsentForSecondRound(
				request.secondMediaUrls(), request.secondCaptionWithHashtags(), request.secondPostUrl());
		}

		// 미디어 합산 개수 제한
		campaignReviewValidationService.validateCombinedMediaLimit(
			request.firstMediaUrls(),
			(typeB != null) ? request.secondMediaUrls() : null
		);

		// 선행/중복 검증 & 저장 A
		campaignReviewGetService.getFirstOrThrow(participation.getId(), typeA);

		CampaignReview secondA = campaignReviewMapper.toSecondReview(
			participation, typeA, request.firstCaptionWithHashtags(), request.firstPostUrl());
		CampaignReview savedA = campaignReviewSaveService.saveReview(secondA);
		campaignReviewSaveService.saveMedia(savedA, request.firstMediaUrls());

		// 2차 리뷰 업로드 시 SocialClip 생성 (성과 지표 0으로 초기화)
		socialClipSaveService.createForSecondReview(savedA);

		// B(옵션)
		if (typeB != null) {
			campaignReviewGetService.getFirstOrThrow(participation.getId(), typeB);
			CampaignReview secondB = campaignReviewMapper.toSecondReview(
				participation, typeB, request.secondCaptionWithHashtags(), request.secondPostUrl());
			CampaignReview savedB = campaignReviewSaveService.saveReview(secondB);
			campaignReviewSaveService.saveMedia(savedB, request.secondMediaUrls());

			// 2차 리뷰 업로드 시 SocialClip 생성 (성과 지표 0으로 초기화)
			socialClipSaveService.createForSecondReview(savedB);
		}

		creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());
		return campaignReviewMapper.toUploadResponse(savedA);
	}

	@Transactional
	public CampaignParticipatedResponse getMyReviewableCampaign(Long userId, Long campaignId, ReviewRound round) {
		Creator creator = creatorGetService.findByUserId(userId);
		return campaignReviewReadService.getMyReviewableCampaign(creator.getId(), campaignId, round);
	}

	@Transactional(readOnly = true)
	public List<CampaignParticipatedResponse> getMyReviewables(Long userId, ReviewRound round) {
		Creator creator = creatorGetService.findByUserId(userId);
		return campaignReviewReadService.getMyReviewables(creator.getId(), round);
	}

	@Transactional(readOnly = true)
	public MediaPresignedUrlResponse createMediaPresignedUrl(Long userId, MediaPresignedUrlRequest request) {
		Creator creator = creatorGetService.findByUserId(userId);
		List<String> urls = campaignReviewUpdateService.createPresignedUrlForReview(creator.getId(), request);

		return campaignReviewMapper.toMediaPresignedUrlResponse(urls);
	}

	/**
	 * 완료된 캠페인의 최종 리뷰 결과 조회
	 * 베타 모드: 1차 리뷰 조회
	 * 정식 모드: 2차 리뷰 조회 (2차가 없으면 1차 리뷰 조회)
	 */
	@Transactional(readOnly = true)
	public CompletedReviewResponse getCompletedReviews(Long userId, Long campaignId) {
		Creator creator = creatorGetService.findByUserId(userId);
		return campaignReviewReadService.getCompletedReviews(creator.getId(), campaignId);
	}
}
