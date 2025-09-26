package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.brand.api.dto.request.BrandNoteRevisionRequest;
import com.lokoko.domain.brand.api.dto.response.BrandNoteRevisionResponse;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.RevisionAction;
import com.lokoko.domain.campaignReview.exception.RevisionRequestNotAllowedException;
import com.lokoko.domain.media.api.dto.request.MediaPresignedUrlRequest;
import com.lokoko.domain.media.api.dto.response.PresignedUrlResponse;
import com.lokoko.domain.media.application.service.S3Service;
import com.lokoko.domain.media.application.utils.MediaValidationUtil;
import com.lokoko.domain.productReview.exception.ErrorMessage;
import com.lokoko.domain.productReview.exception.InvalidMediaTypeException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignReviewUpdateService {

    private final S3Service s3Service;
    private final CampaignReviewGetService campaignReviewGetService;

    /**
     * 브랜드가 크리에이터 리뷰에 대해 수정 요청을 남기는 메서드
     * <p>
     * - 브랜드가 해당 캠페인을 소유하지 않으면 예외 발생 - 대상 리뷰가 1차 제출 상태가 아니면 수정 요청 불가 - RevisionAction 에 따라 임시 저장(SAVE_DRAFT) 또는 최종
     * 제출(SUBMIT) 처리
     *
     * @param action           수정 요청 액션 (임시저장 / 제출)
     * @param brandId          브랜드 ID
     * @param campaignReviewId 대상 리뷰 ID
     * @param revisionRequest  수정 요청 DTO (브랜드 노트 포함)
     * @return 수정 요청 처리 결과 응답 DTO
     * @throws NotCampaignOwnershipException      브랜드가 해당 캠페인을 소유하지 않은 경우
     * @throws RevisionRequestNotAllowedException 리뷰 상태가 SUBMITTED 가 아닌 경우
     */
    @Transactional
    public BrandNoteRevisionResponse requestReviewRevision(RevisionAction action, Long brandId, Long campaignReviewId,
                                                           BrandNoteRevisionRequest revisionRequest) {
        CampaignReview campaignReview = campaignReviewGetService.findById(campaignReviewId);

        validateBrandOwnsCampaign(brandId, campaignReview);
        validateReviewStatus(campaignReview);
        String brandNote = revisionRequest.brandNote();

        if (action == RevisionAction.SAVE_DRAFT) {
            campaignReview.saveRequestRevision(brandNote);
        } else if (action == RevisionAction.SUBMIT) {
            campaignReview.submitRequestRevision(brandNote);
        }

        return new BrandNoteRevisionResponse(
                campaignReview.getBrandNote(),
                campaignReview.getBrandNoteStatus(),
                campaignReview.getRevisionRequestedAt()
        );
    }

    /**
     * 리뷰 업로드용 프리사인드 URL을 생성하는 메서드
     * <p>
     * - 요청된 미디어 타입이 없거나 비어있으면 예외 발생 - 개수 제한 (최대 15개) 검증 - image/* 또는 video/* MIME 타입만 허용
     *
     * @param creatorId 요청자(크리에이터) ID
     * @param request   미디어 Presigned URL 요청 DTO
     * @return 발급된 Presigned URL 리스트
     * @throws InvalidMediaTypeException 지원되지 않는 형식 또는 개수 제한 초과 시 발생
     */
    @Transactional(readOnly = true)
    public List<String> createPresignedUrlForReview(Long creatorId, MediaPresignedUrlRequest request) {
        List<String> mediaTypes = request.mediaType();

        if (mediaTypes == null || mediaTypes.isEmpty()) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }

        MediaValidationUtil.validateTotalMediaCount(mediaTypes);

        boolean allOk = mediaTypes.stream().allMatch(t -> t.startsWith("image/") || t.startsWith("video/"));
        if (!allOk) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }

        return mediaTypes.stream()
                .map(s3Service::generatePresignedUrl)
                .map(PresignedUrlResponse::presignedUrl)
                .toList();
    }


    private static void validateBrandOwnsCampaign(Long brandId, CampaignReview campaignReview) {
        if (!campaignReview.getCreatorCampaign().getCampaign().getBrand().getId().equals(brandId)) {
            throw new NotCampaignOwnershipException();
        }
    }

    private static void validateReviewStatus(CampaignReview review) {
        if (review.getStatus() != ReviewStatus.SUBMITTED) {
            throw new RevisionRequestNotAllowedException();
        }
    }
}
