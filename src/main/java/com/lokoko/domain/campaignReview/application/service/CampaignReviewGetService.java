package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewImageRepository;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewRepository;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewVideoRepository;
import com.lokoko.domain.campaignReview.exception.CampaignReviewNotFoundException;
import com.lokoko.domain.campaignReview.exception.FirstReviewNotFoundException;
import com.lokoko.domain.campaignReview.exception.ReviewAlreadySubmittedException;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
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

    /**
     * 리뷰 ID로 {@link CampaignReview} 를 조회. 존재하지 않을 경우 {@link CampaignReviewNotFoundException} 발생.
     *
     * @param campaignReviewId 조회할 리뷰 ID
     * @return 조회된 {@link CampaignReview} 엔티티
     * @throws CampaignReviewNotFoundException 해당 ID의 리뷰가 없을 경우
     */
    public CampaignReview findById(Long campaignReviewId) {
        return campaignReviewRepository.findById(campaignReviewId)
                .orElseThrow(CampaignReviewNotFoundException::new);
    }

    /**
     * CreatorCampaign의 1차(FIRST) 리뷰 중 가장 먼저 생성된 리뷰의 ContentType 조회 메서드 - 다건 가능성을 고려하여 정렬 후 첫 번째 요소만 반환.
     *
     * @param creatorCampaignId 조회할 CreatorCampaign ID
     * @return Optional<ContentType> 존재하면 ContentType, 없으면 Optional.empty()
     */
    public Optional<ContentType> findFirstContentType(Long creatorCampaignId) {
        List<ContentType> types =
                campaignReviewRepository.findContentOnly(creatorCampaignId, ReviewRound.FIRST);
        return types.stream().findFirst();
    }

    /**
     * 동일 Campaign 내 특정 ContentType의 1차(FIRST) 리뷰가 이미 존재하는지 검증. 존재할 경우 예외 던짐
     *
     * @param creatorCampaignId CreatorCampaign ID
     * @param type              검증할 {@link ContentType}
     * @throws ReviewAlreadySubmittedException 동일 타입의 1차 리뷰가 이미 존재할 경우
     */
    public void getFirstContent(Long creatorCampaignId, ContentType type) {
        if (campaignReviewRepository.existsByCreatorCampaignIdAndReviewRoundAndContentType(
                creatorCampaignId, ReviewRound.FIRST, type)) {
            throw new ReviewAlreadySubmittedException();
        }
    }

    /**
     * (2차 업로드용) 특정 CreatorCampaign + ContentType 조합으로 1차(FIRST) 리뷰 조회 메서드 - 없을 경우 예외 던짐
     *
     * @param creatorCampaignId CreatorCampaign ID
     * @param type              조회할 {@link ContentType}
     * @return 조회된 {@link CampaignReview} 엔티티
     * @throws FirstReviewNotFoundException 해당 조건의 1차 리뷰가 없을 경우
     */
    public CampaignReview getFirstOrThrow(Long creatorCampaignId, ContentType type) {
        return campaignReviewRepository
                .findTopByCreatorCampaignIdAndReviewRoundAndContentTypeOrderByIdAsc(
                        creatorCampaignId, ReviewRound.FIRST, type)
                .orElseThrow(FirstReviewNotFoundException::new);
    }

    /**
     * 특정 CreatorCampaign 내 동일 ContentType의 2차(SECOND) 리뷰 존재 여부 검증. 이미 존재하면 {@link ReviewAlreadySubmittedException} 발생.
     *
     * @param creatorCampaignId CreatorCampaign ID
     * @param type              검증할 {@link ContentType}
     * @throws ReviewAlreadySubmittedException 동일 타입의 2차 리뷰가 이미 존재할 경우
     */
    public void assertSecondNotExists(Long creatorCampaignId, ContentType type) {
        boolean exists = campaignReviewRepository
                .existsByCreatorCampaignIdAndReviewRoundAndContentType(
                        creatorCampaignId, ReviewRound.SECOND, type);
        if (exists) {
            throw new ReviewAlreadySubmittedException();
        }
    }

    /**
     * 리뷰에 업로드된 미디어 URL들을 displayOrder 기준 오름차순으로 반환. 이미지가 존재할 경우 이미지 리스트만 반환, 없을 경우 비디오 리스트 반환.
     *
     * @param campaignReview 대상 {@link CampaignReview}
     * @return 정렬된 미디어 URL 리스트 (없으면 빈 리스트)
     */
    public List<String> getOrderedMediaUrls(CampaignReview campaignReview) {
        List<String> images = campaignReviewImageRepository
                .findImageUrlsByReviewIdOrderByDisplay(campaignReview.getId());
        if (!images.isEmpty()) {
            return images;
        }
        return campaignReviewVideoRepository
                .findVideoUrlsByReviewIdOrderByDisplay(campaignReview.getId());
    }

    /**
     * CreatorCampaign과 라운드(FIRST/SECOND)로 리뷰 “목록” 조회. 여러 건 가능성을 고려해 id DESC로 정렬하여 반환.
     */
    public List<CampaignReview> getAllByCreatorCampaignAndRound(
            CreatorCampaign creatorCampaign, ReviewRound reviewRound) {
        return campaignReviewRepository
                .findByCreatorCampaignAndReviewRoundOrderByIdDesc(creatorCampaign, reviewRound);
    }

    /**
     * 특정 CreatorCampaign 내 라운드 존재 여부
     */
    public boolean existsRound(Long creatorCampaignId, ReviewRound reviewRound) {
        return campaignReviewRepository.existsByCreatorCampaignIdAndReviewRound(creatorCampaignId, reviewRound);
    }

    /**
     * 해당 CreatorCampaign의 가장 최신(최대 id) 1차(FIRST) 리뷰 1건을 Optional로 반환 - 여러 FIRST가 있을 수 있으므로 id DESC 정렬 후 첫 요소만 선택한다.
     *
     * @param creatorCampaign 조회 대상 CreatorCampaign
     * @return 최신 FIRST 리뷰 (없으면 Optional.empty())
     */
    public Optional<CampaignReview> findLatestFirst(CreatorCampaign creatorCampaign) {

        return campaignReviewRepository.findTopByCreatorCampaignAndReviewRoundOrderByIdDesc(creatorCampaign,
                ReviewRound.FIRST);
    }

    public boolean existsFirst(Long creatorCampaignId) {
        return existsRound(creatorCampaignId, ReviewRound.FIRST);
    }

    public boolean existsSecond(Long creatorCampaignId) {
        return existsRound(creatorCampaignId, ReviewRound.SECOND);
    }
}
