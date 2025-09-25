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
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.exception.MismatchedContentTypeException;
import com.lokoko.domain.creator.application.service.CreatorGetService;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.media.api.dto.request.MediaPresignedUrlRequest;
import com.lokoko.domain.media.api.dto.response.MediaPresignedUrlResponse;
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
     * 1차 리뷰 업로드 - 동일 캠페인 내 같은 ContentType의 1차가 이미 있으면 409
     * <p> - 1차 리뷰는 동일 캠페인에 대해서 여러개 가능 (타입만 다르면 허용 ex. 인스타, 틱톡)
     */
    @Transactional
    public ReviewUploadResponse uploadFirst(Long userId, Long campaignId, FirstReviewUploadRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        Campaign campaign = campaignGetService.findByCampaignId(campaignId);
        CreatorCampaign participation = creatorCampaignGetService.getByCampaignAndCreatorId(campaign, creator.getId());

        campaignReviewGetService.getFirstContent(participation.getId(), request.contentType());

        CampaignReview toSave = campaignReviewMapper.toFirstReview(participation, request);
        CampaignReview saved = campaignReviewSaveService.saveReview(toSave);
        campaignReviewSaveService.saveMedia(saved, request.mediaUrls());

        creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());

        return campaignReviewMapper.toUploadResponse(saved);
    }

    /**
     * 2차 리뷰 업로드 - path/body로 전달받은 firstReviewId 기준으로:
     * <p> - (a) 해당 1차가 본인 소유인지 + 진짜 1차인지 확인 (아니면 404/권한불일치)
     * <p> - (b) 동일 타입의 2차가 이미 붙어 있으면 409
     * <p> - (c) 요청 contentType은 1차와 동일해야 함(불일치시 400)
     */
    @Transactional
    public ReviewUploadResponse uploadSecond(Long userId, Long firstReviewId, SecondReviewUploadRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        CampaignReview first = campaignReviewGetService.getFirstReviewWithOwnershipCheck(firstReviewId,
                creator.getId());

        campaignReviewGetService.getSecondNotExistsForFirst(firstReviewId);

        if (request.contentType() != null && request.contentType() != first.getContentType()) {
            throw new MismatchedContentTypeException();
        }

        CreatorCampaign participation = first.getCreatorCampaign();
        CampaignReview toSave = campaignReviewMapper.toSecondReview(participation, request);
        CampaignReview saved = campaignReviewSaveService.saveReview(toSave);
        campaignReviewSaveService.saveMedia(saved, request.mediaUrls());

        creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());

        return campaignReviewMapper.toUploadResponse(saved);
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
