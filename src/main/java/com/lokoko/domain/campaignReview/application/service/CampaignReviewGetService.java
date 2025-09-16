package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewRepository;
import com.lokoko.domain.campaignReview.exception.ReviewAlreadySubmittedException;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignReviewGetService {

    private final CampaignReviewRepository campaignReviewRepository;

    /**
     * 특정 캠페인에 대해 지정된 리뷰 라운드가 이미 존재하는지 조회하는 메서드 - 만약 해당 라운드의 리뷰가 이미 제출된 상태면 예외 발생
     *
     * @param creatorCampaignId 확인할 크리에이터 캠페인 ID
     * @param reviewRound       확인할 리뷰 라운드 (예: 1차, 2차)
     * @throws ReviewAlreadySubmittedException 이미 해당 라운드의 리뷰가 존재할 경우 발생
     */
    public void findExistingReviewRound(Long creatorCampaignId, ReviewRound reviewRound) {
        if (campaignReviewRepository.existsByCreatorCampaignIdAndReviewRound(creatorCampaignId, reviewRound)) {
            throw new ReviewAlreadySubmittedException();
        }
    }

    /**
     * 특정 크리에이터 캠페인의 1차 리뷰에 등록된 콘텐츠 유형만 조회하는 메서드 - 데이터가 존재하지 않을 경우 빈배열을 반환
     *
     * @param creatorCampaignId 조회할 크리에이터 캠페인 ID
     * @return 1차 리뷰에 등록된 {@link ContentType}, 없으면 Optional.empty()
     */
    public Optional<ContentType> findFirstContent(Long creatorCampaignId) {
        return campaignReviewRepository.findContentOnly(creatorCampaignId, ReviewRound.FIRST);
    }

    /**
     * 특정 CreatorCampaign 내 라운드 존재 여부
     */
    public boolean existsRound(Long creatorCampaignId, ReviewRound reviewRound) {
        return campaignReviewRepository.existsByCreatorCampaignIdAndReviewRound(creatorCampaignId, reviewRound);
    }

    public boolean existsFirst(Long creatorCampaignId) {
        return existsRound(creatorCampaignId, ReviewRound.FIRST);
    }

    public boolean existsSecond(Long creatorCampaignId) {
        return existsRound(creatorCampaignId, ReviewRound.SECOND);
    }
}
