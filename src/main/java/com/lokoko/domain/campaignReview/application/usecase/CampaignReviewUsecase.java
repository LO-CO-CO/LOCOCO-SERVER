package com.lokoko.domain.campaignReview.application.usecase;


import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.application.mapper.CampaignMapper;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.api.dto.request.FirstReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.request.SecondReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.response.ReviewUploadResponse;
import com.lokoko.domain.campaignReview.application.mapper.CampaignReviewMapper;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewSaveService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewUpdateService;
import com.lokoko.domain.campaignReview.application.service.CreatorCampaignUpdateService;
import com.lokoko.domain.campaignReview.application.utils.CampaignReviewValidationUtil;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.creator.application.service.CreatorGetService;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.media.api.dto.request.MediaPresignedUrlRequest;
import com.lokoko.domain.media.api.dto.response.MediaPresignedUrlResponse;
import com.lokoko.domain.media.application.utils.MediaValidationUtil;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final CampaignReviewMapper campaignReviewMapper;
    private final CampaignMapper campaignMapper;

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
        CampaignReviewValidationUtil.validateTwoSetCombination(typeA, typeB);

        // A 세트(캠페인에 second가 없는 단일 타입 캠페인)
        CampaignReviewValidationUtil.requireFirstSetPresent(request.firstMediaUrls(),
                request.firstCaptionWithHashtags());
        MediaValidationUtil.validateTotalMediaCount(request.firstMediaUrls());

        // B 세트(캠페인에 second가 있으면 필수, 없으면 금지)
        if (typeB != null) {
            CampaignReviewValidationUtil.requireFirstSetPresent(
                    request.secondMediaUrls(), request.secondCaptionWithHashtags());
            MediaValidationUtil.validateTotalMediaCount(request.secondMediaUrls());
        } else {
            CampaignReviewValidationUtil.ensureSecondSetAbsentForFirstRound(
                    request.secondMediaUrls(), request.secondCaptionWithHashtags());
        }

        // 미디어 합산 개수 제한
        CampaignReviewValidationUtil.validateCombinedMediaLimit(
                request.firstMediaUrls(),
                (typeB != null) ? request.secondMediaUrls() : null
        );

        // 동일 타입 FIRST 중복 방지
        campaignReviewGetService.getFirstContent(participation.getId(), typeA);
        if (typeB != null) {
            campaignReviewGetService.getFirstContent(participation.getId(), typeB);
        }

        // 저장 A
        CampaignReview firstA = campaignReviewMapper.toFirstReview(
                participation, typeA, request.firstCaptionWithHashtags());
        CampaignReview savedA = campaignReviewSaveService.saveReview(firstA);
        campaignReviewSaveService.saveMedia(savedA, request.firstMediaUrls());

        // 저장 B(옵션)
        if (typeB != null) {
            CampaignReview firstB = campaignReviewMapper.toFirstReview(
                    participation, typeB, request.secondCaptionWithHashtags());
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
        CampaignReviewValidationUtil.validateTwoSetCombination(typeA, typeB);

        // A 세트(필수: 미디어/캡션/postUrl)
        CampaignReviewValidationUtil.requireSecondSetPresent(
                request.firstMediaUrls(), request.firstCaptionWithHashtags(), request.firstPostUrl());
        MediaValidationUtil.validateTotalMediaCount(request.firstMediaUrls());

        // B 세트(캠페인에 second가 있으면 필수, 없으면 금지)
        if (typeB != null) {
            CampaignReviewValidationUtil.requireSecondSetPresent(
                    request.secondMediaUrls(), request.secondCaptionWithHashtags(), request.secondPostUrl());
            MediaValidationUtil.validateTotalMediaCount(request.secondMediaUrls());
        } else {
            CampaignReviewValidationUtil.ensureSecondSetAbsentForSecondRound(
                    request.secondMediaUrls(), request.secondCaptionWithHashtags(), request.secondPostUrl());
        }

        // 미디어 합산 개수 제한
        CampaignReviewValidationUtil.validateCombinedMediaLimit(
                request.firstMediaUrls(),
                (typeB != null) ? request.secondMediaUrls() : null
        );

        // 선행/중복 검증 & 저장 A
        campaignReviewGetService.getFirstOrThrow(participation.getId(), typeA);
        campaignReviewGetService.assertSecondNotExists(participation.getId(), typeA);

        CampaignReview secondA = campaignReviewMapper.toSecondReview(
                participation, typeA, request.firstCaptionWithHashtags(), request.firstPostUrl());
        CampaignReview savedA = campaignReviewSaveService.saveReview(secondA);
        campaignReviewSaveService.saveMedia(savedA, request.firstMediaUrls());

        // B(옵션)
        if (typeB != null) {
            campaignReviewGetService.getFirstOrThrow(participation.getId(), typeB);
            campaignReviewGetService.assertSecondNotExists(participation.getId(), typeB);
            CampaignReview secondB = campaignReviewMapper.toSecondReview(
                    participation, typeB, request.secondCaptionWithHashtags(), request.secondPostUrl());
            CampaignReview savedB = campaignReviewSaveService.saveReview(secondB);
            campaignReviewSaveService.saveMedia(savedB, request.secondMediaUrls());
        }

        creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());
        return campaignReviewMapper.toUploadResponse(savedA);
    }

    @Transactional(readOnly = true)
    public List<CampaignParticipatedResponse> getMyReviewableCampaigns(Long userId) {
        Creator creator = creatorGetService.findByUserId(userId);
        List<CreatorCampaign> eligible = creatorCampaignGetService.findReviewable(creator.getId());

        return eligible.stream()
                .map(campaignMapper::toCampaignParticipationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MediaPresignedUrlResponse createMediaPresignedUrl(Long userId, MediaPresignedUrlRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        List<String> urls = campaignReviewUpdateService.createPresignedUrlForReview(creator.getId(), request);

        return campaignReviewMapper.toMediaPresignedUrlResponse(urls);
    }
}
