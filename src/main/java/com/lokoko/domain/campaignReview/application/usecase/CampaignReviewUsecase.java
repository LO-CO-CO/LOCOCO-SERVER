package com.lokoko.domain.campaignReview.application.usecase;


import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.application.mapper.CampaignMapper;
import com.lokoko.domain.campaignReview.api.dto.request.FirstReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.request.SecondReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.response.ReviewUploadResponse;
import com.lokoko.domain.campaignReview.application.mapper.CampaignReviewMapper;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewSaveService;
import com.lokoko.domain.campaignReview.application.service.CreatorCampaignUpdateService;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.exception.FirstReviewNotFoundException;
import com.lokoko.domain.campaignReview.exception.MismatchedContentTypeException;
import com.lokoko.domain.creator.application.service.CreatorGetService;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignReviewUsecase {

    private final CreatorGetService creatorGetService;
    private final CampaignReviewGetService campaignReviewGetService;
    private final CreatorCampaignGetService creatorCampaignGetService;

    private final CampaignReviewSaveService campaignReviewSaveService;
    private final CreatorCampaignUpdateService creatorCampaignUpdateService;

    private final CampaignReviewMapper campaignReviewMapper;
    private final CampaignMapper campaignMapper;

    @Transactional
    public ReviewUploadResponse uploadFirst(Long userId, Long campaignId, FirstReviewUploadRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        CreatorCampaign participation = creatorGetService.findParticipation(campaignId, creator.getId());

        campaignReviewGetService.findExistingReviewRound(participation.getId(), ReviewRound.FIRST);

        CampaignReview toSave = campaignReviewMapper.toFirstReview(participation, request);
        CampaignReview saved = campaignReviewSaveService.saveReview(toSave);
        campaignReviewSaveService.saveMedia(saved, request.mediaUrls());

        creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());

        return campaignReviewMapper.toUploadResponse(saved);
    }

    @Transactional
    public ReviewUploadResponse uploadSecond(Long userId, Long campaignId, SecondReviewUploadRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        CreatorCampaign participation = creatorGetService.findParticipation(campaignId, creator.getId());

        campaignReviewGetService.findExistingReviewRound(participation.getId(), ReviewRound.SECOND);

        // 2차는 1차 리뷰가 선행되어야 함
        if (!campaignReviewGetService.existsFirst(participation.getId())) {
            throw new FirstReviewNotFoundException();
        }

        // 1차와 동일 포맷만 허용
        campaignReviewGetService.findFirstContent(participation.getId())
                .ifPresent(first -> {
                    if (!first.equals(request.contentType())) {
                        throw new MismatchedContentTypeException();
                    }
                });

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
}
