package com.lokoko.domain.campaignReview.application.usecase;


import com.lokoko.domain.campaign.domain.entity.CreatorCampaign;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignReviewUsecase {

    private final CreatorGetService creatorGetService;
    private final CampaignReviewGetService campaignReviewGetService;

    private final CampaignReviewSaveService campaignReviewSaveService;
    private final CreatorCampaignUpdateService creatorCampaignUpdateService;

    private final CampaignReviewMapper campaignReviewMapper;

    @Transactional
    public ReviewUploadResponse uploadFirst(Long userId, Long campaignId, FirstReviewUploadRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        CreatorCampaign participation = creatorGetService.findParticipation(campaignId, creator.getId());

        campaignReviewGetService.assertRoundNotExists(participation.getId(), ReviewRound.FIRST);

        CampaignReview toSave = campaignReviewMapper.toFirstReview(participation, request);
        CampaignReview saved = campaignReviewSaveService.saveReview(toSave);
        campaignReviewSaveService.saveImages(saved, request.imageUrls());

        creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());

        return campaignReviewMapper.toUploadResponse(saved);
    }

    @Transactional
    public ReviewUploadResponse uploadSecond(Long userId, Long campaignId, SecondReviewUploadRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        CreatorCampaign participation = creatorGetService.findParticipation(creator.getId(), campaignId);

        campaignReviewGetService.assertRoundNotExists(participation.getId(), ReviewRound.SECOND);

        // 2차는 1차 리뷰가 선행되어야 함
        if (!campaignReviewGetService.existsFirst(participation.getId())) {
            throw new FirstReviewNotFoundException();
        }

        // 1차와 동일 포맷만 허용
        campaignReviewGetService.findFirstContent(participation.getId())
                .ifPresent(first -> {
                    if (!first.equals(request.content())) {
                        throw new MismatchedContentTypeException();
                    }
                });

        CampaignReview toSave = campaignReviewMapper.toSecondReview(participation, request);
        CampaignReview saved = campaignReviewSaveService.saveReview(toSave);
        campaignReviewSaveService.saveImages(saved, request.imageUrls());

        creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());

        return campaignReviewMapper.toUploadResponse(saved);
    }
}
