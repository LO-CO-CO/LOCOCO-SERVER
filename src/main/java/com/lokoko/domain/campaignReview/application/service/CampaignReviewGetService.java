package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewImageRepository;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewRepository;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewVideoRepository;
import com.lokoko.domain.campaignReview.exception.CampaignReviewNotFoundException;
import com.lokoko.domain.campaignReview.exception.FirstReviewNotFoundException;
import com.lokoko.domain.campaignReview.exception.ReviewAlreadySubmittedException;
import com.lokoko.domain.creator.exception.CreatorCampaignNotFoundException;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignReviewGetService {

    private final CampaignReviewRepository campaignReviewRepository;
    private final CampaignReviewImageRepository campaignReviewImageRepository;
    private final CampaignReviewVideoRepository campaignReviewVideoRepository;

    public CampaignReview findById(Long campaignReviewId) {
        return campaignReviewRepository.findById(campaignReviewId)
                .orElseThrow(CampaignReviewNotFoundException::new);
    }

    /**
     * 특정 CreatorCampaign 내 지정 라운드(1차/2차)의 리뷰가 이미 존재하는지 검증하는 메서드 - 존재하면 예외를 던짐
     *
     * @param creatorCampaignId CreatorCampaign ID
     * @param reviewRound       FIRST 또는 SECOND
     */
    public void getExistingReviewRound(Long creatorCampaignId, ReviewRound reviewRound) {
        if (campaignReviewRepository.existsByCreatorCampaignIdAndReviewRound(creatorCampaignId, reviewRound)) {
            throw new ReviewAlreadySubmittedException();
        }
    }


    /**
     * 1차 업로드 - 동일 Campaign내 같은 ContentType의 1차 리뷰가 이미 존재하는지 검증하는 메서드 - 존재하면 예외 던짐
     *
     * @param creatorCampaignId CreatorCampaign ID
     * @param type              업로드하려는 1차 리뷰의 ContentType
     */
    public void getFirstContent(Long creatorCampaignId, ContentType type) {
        if (campaignReviewRepository.existsByCreatorCampaignIdAndReviewRoundAndContentType(
                creatorCampaignId, ReviewRound.FIRST, type)) {

            throw new ReviewAlreadySubmittedException();
        }
    }

    /**
     * 1차 리뷰 ID가 실제 1차(FIRST)인지, 그리고 해당 1차가 현재 유저(creatorId)의 소유인지 검증하고 엔티티를 반환하는 메서드 - 조건 불일치 시 예외 던짐
     *
     * @param firstReviewId 1차 리뷰 ID
     * @param creatorId     현재 유저의 Creator ID
     */
    public CampaignReview getFirstReviewWithOwnershipCheck(Long firstReviewId, Long creatorId) {
        CampaignReview first = campaignReviewRepository.findWithCreatorCampaignById(firstReviewId)
                .orElseThrow(FirstReviewNotFoundException::new);

        if (first.getReviewRound() != ReviewRound.FIRST) {
            throw new FirstReviewNotFoundException();
        }
        if (!first.getCreatorCampaign().getCreator().getId().equals(creatorId)) {
            throw new CreatorCampaignNotFoundException();
        }
        return first;
    }

    /**
     * 특정 1차 리뷰(의 타입)에 대한 2차 리뷰가 이미 존재하는지 검증하는 메서드 - 존재하면 예외 던짐
     *
     * @param firstReviewId 1차 리뷰 ID
     */
    public void getSecondNotExistsForFirst(Long firstReviewId) {
        CampaignReview first = campaignReviewRepository.findWithCreatorCampaignById(firstReviewId)
                .orElseThrow(FirstReviewNotFoundException::new);

        boolean existsSecondSameType = campaignReviewRepository
                .existsByCreatorCampaignIdAndReviewRoundAndContentType(
                        first.getCreatorCampaign().getId(),
                        ReviewRound.SECOND,
                        first.getContentType());

        if (existsSecondSameType) {
            throw new ReviewAlreadySubmittedException();
        }
    }

    /**
     * 리뷰에 업로드된 미디어 URL 리스트를 displayOrder 오름차순으로 반환하는 메서드 - 이미지가 하나라도 있으면 이미지들만 반환하고, 그렇지 않으면 비디오들을 반환 (N+1 방지)
     *
     * @param campaignReview 대상 리뷰 엔티티
     * @return 정렬된 미디어 URL 리스트 (없으면 빈 리스트)
     */
    public List<String> getOrderedMediaUrls(CampaignReview campaignReview) {
        List<String> imageUrls =
                campaignReviewImageRepository.findImageUrlsByReviewIdOrderByDisplay(campaignReview.getId());
        if (!imageUrls.isEmpty()) {
            return imageUrls;
        }
        return campaignReviewVideoRepository.findVideoUrlsByReviewIdOrderByDisplay(campaignReview.getId());
    }

    /**
     * CreatorCampaign과 리뷰 라운드로 리뷰 단건을 조회 메서드 - 존재하지 않으면 예외 발생
     *
     * @param creatorCampaign 참여 엔티티
     * @param reviewRound     리뷰 라운드(FIRST/SECOND)
     * @return CampaignReview
     */
    public CampaignReview getByCreatorCampaignAndRound(CreatorCampaign creatorCampaign, ReviewRound reviewRound) {
        return campaignReviewRepository.findByCreatorCampaignAndReviewRound(creatorCampaign, reviewRound)
                .orElseThrow(CampaignReviewNotFoundException::new);
    }

    /**
     * 특정 CreatorCampaign 내 라운드 존재 여부
     */
    public boolean existsRound(Long creatorCampaignId, ReviewRound reviewRound) {
        return campaignReviewRepository.existsByCreatorCampaignIdAndReviewRound(creatorCampaignId, reviewRound);
    }

    /**
     * 목록/뷰용: CreatorCampaign의 1차(FIRST) 리뷰 중 하나의 ContentType을 조회하는 메서드 - 없으면 Optional.empty()를 반환
     *
     * @param creatorCampaignId CreatorCampaign ID
     * @return Optional<ContentType>
     */
    public Optional<ContentType> findFirstContentType(Long creatorCampaignId) {
        return campaignReviewRepository.findContentOnly(creatorCampaignId, ReviewRound.FIRST);
    }

    public boolean existsFirst(Long creatorCampaignId) {
        return existsRound(creatorCampaignId, ReviewRound.FIRST);
    }

    public boolean existsSecond(Long creatorCampaignId) {
        return existsRound(creatorCampaignId, ReviewRound.SECOND);
    }
}
