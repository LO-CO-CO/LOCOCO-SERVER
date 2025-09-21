package com.lokoko.domain.campaign.application.service;

import static com.lokoko.global.utils.AllowedMediaType.ALLOWED_MEDIA_TYPES;

import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.brand.domain.repository.BrandRepository;
import com.lokoko.domain.brand.exception.BrandNotFoundException;
import com.lokoko.domain.campaign.api.dto.request.CampaignCreateRequest;
import com.lokoko.domain.campaign.api.dto.request.CampaignDraftRequest;
import com.lokoko.domain.campaign.api.dto.request.CampaignMediaRequest;
import com.lokoko.domain.campaign.api.dto.request.CampaignPublishRequest;
import com.lokoko.domain.campaign.api.dto.response.CampaignBasicResponse;
import com.lokoko.domain.campaign.api.dto.response.CampaignImageResponse;
import com.lokoko.domain.campaign.api.dto.response.CampaignMediaResponse;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.ActionType;
import com.lokoko.domain.campaign.domain.repository.CampaignRepository;
import com.lokoko.domain.campaign.exception.CampaignNotEditableException;
import com.lokoko.domain.campaign.exception.CampaignNotFoundException;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.image.domain.entity.CampaignImage;
import com.lokoko.domain.image.domain.entity.enums.ImageType;
import com.lokoko.domain.image.domain.repository.CampaignImageRepository;
import com.lokoko.domain.productReview.exception.ErrorMessage;
import com.lokoko.domain.productReview.exception.InvalidMediaTypeException;
import com.lokoko.global.common.dto.PresignedUrlResponse;
import com.lokoko.global.common.service.S3Service;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignImageRepository campaignImageRepository;
    private final BrandRepository brandRepository;
    private final S3Service s3Service;

    public CampaignMediaResponse createMediaPresignedUrl(Long brandId, CampaignMediaRequest request) {

        brandRepository.findById(brandId).orElseThrow(BrandNotFoundException::new);

        List<String> mediaTypes = request.mediaType();
        // 허용되지 않은 형식이 있는지 검증
        for (String type : mediaTypes) {
            if (!ALLOWED_MEDIA_TYPES.contains(type)) {
                throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
            }
        }
        // presigned URL 발급
        List<String> urls = mediaTypes.stream()
                .map(s3Service::generatePresignedUrl)
                .map(PresignedUrlResponse::presignedUrl)
                .toList();

        return new CampaignMediaResponse(urls);
    }

    @Transactional
    public CampaignBasicResponse createCampaignDraft(Long brandId, CampaignDraftRequest draftRequest) {
        CampaignCreateRequest createRequest = CampaignCreateRequest.convertDraftToCreateRequest(draftRequest);
        return createCampaignWithAction(brandId, ActionType.SAVE_DRAFT, createRequest);
    }

    @Transactional
    public CampaignBasicResponse createAndPublishCampaign(Long brandId, CampaignPublishRequest publishRequest) {
        CampaignCreateRequest createRequest = CampaignCreateRequest.convertPublishToCreateRequest(publishRequest);
        return createCampaignWithAction(brandId, ActionType.PUBLISH, createRequest);
    }

    @Transactional
    public CampaignBasicResponse updateCampaignToDraft(Long brandId, Long campaignId,
                                                       CampaignDraftRequest draftRequest) {
        CampaignCreateRequest updateRequest = CampaignCreateRequest.convertDraftToCreateRequest(draftRequest);
        return updateCampaign(brandId, campaignId, ActionType.SAVE_DRAFT, updateRequest);
    }

    @Transactional
    public CampaignBasicResponse updateAndPublishCampaign(Long brandId, Long campaignId,
                                                          CampaignPublishRequest publishRequest) {
        CampaignCreateRequest updateRequest = CampaignCreateRequest.convertPublishToCreateRequest(publishRequest);
        return updateCampaign(brandId, campaignId, ActionType.PUBLISH, updateRequest);
    }

    @Transactional
    public CampaignBasicResponse createCampaignWithAction(Long brandId, ActionType actionType,
                                                          CampaignCreateRequest createRequest) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(BrandNotFoundException::new);

        Campaign campaign = Campaign.createCampaign(createRequest, brand);

        validatePublishableCampaign(actionType, campaign);

        Campaign savedCampaign = campaignRepository.save(campaign);
        List<CampaignImage> savedImages = saveImages(createRequest, savedCampaign);

        return buildCampaignCreateResponse(savedCampaign, savedImages);
    }


    private List<CampaignImage> saveImages(CampaignCreateRequest createRequest, Campaign campaign) {

        List<CampaignImage> toSaveImages = Stream.of(
                        createRequest.topImages(),
                        createRequest.bottomImages()
                )
                .flatMap(Collection::stream)
                .map(img -> CampaignImage.createCampaignImage(
                        img.url(),
                        img.displayOrder(),
                        img.imageType(),
                        campaign))
                .collect(Collectors.toList());

        return campaignImageRepository.saveAll(toSaveImages);
    }

    private CampaignBasicResponse buildCampaignCreateResponse(
            Campaign campaign, List<CampaignImage> savedImages) {

        List<CampaignImageResponse> topImages = savedImages.stream()
                .filter(img -> img.getImageType() == ImageType.TOP)
                .sorted(Comparator.comparing(CampaignImage::getDisplayOrder))
                .map(CampaignImageResponse::from)
                .toList();

        List<CampaignImageResponse> bottomImages = savedImages.stream()
                .filter(img -> img.getImageType() == ImageType.BOTTOM)
                .sorted(Comparator.comparing(CampaignImage::getDisplayOrder))
                .map(CampaignImageResponse::from)
                .toList();

        return CampaignBasicResponse.of(campaign, topImages, bottomImages);

    }

    @Transactional
    public CampaignBasicResponse updateCampaign(Long brandId, Long campaignId, ActionType actionType,
                                                CampaignCreateRequest updateRequest) {

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(BrandNotFoundException::new);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        validateBrandOwnsCampaign(campaign, brand);

        validateEditableCampaign(campaign);

        campaign.updateCampaign(updateRequest);

        validatePublishableCampaign(actionType, campaign);

        // 기존에 존재하는 이미지 삭제후 갈아끼우기
        campaignImageRepository.deleteByCampaignId(campaignId);
        List<CampaignImage> savedImages = saveImages(updateRequest, campaign);

        return buildCampaignCreateResponse(campaign, savedImages);
    }

    /**
     * 캠페인이 발행가능한 캠페인인지 검증한다 <br> 즉, 캠페인이 초안 상태인지 검증한다. <br> actionType 이 PUBLISH 인 경우에만 검증을 수행한다.(발행 시점에 초안 상태이면 안
     * 되므로)
     * <br> actionType 이 SAVE_DRAFT 인 경우에는 검증을 수행하지 않는다. (임시저장은 필드가 다 채워지지 않아도 상관 없으므로)
     *
     * @param actionType 임시저장 / 발행 여부
     * @param campaign   캠페인 엔티티
     */
    private static void validatePublishableCampaign(ActionType actionType, Campaign campaign) {
        if (actionType == ActionType.PUBLISH) {
            campaign.validatePublishable();
            campaign.publish();
        }
    }

    /**
     * 캠페인이 수정 가능한지 검증한다. <br> 캠페인이 이미 발행되었으면 예외를 발생시킨다.
     *
     * @param campaign 캠페인 엔티티
     * @throws CampaignNotEditableException 캠페인이 수정 불가할 때 발생하는 예외
     */
    private void validateEditableCampaign(Campaign campaign) {
        if (campaign.isPublished()) {
            throw new CampaignNotEditableException();
        }
    }

    /**
     * 캠페인이 브랜드 소유인지 검증한다. <br> 캠페인이 브랜드 소유가 아니라면 예외를 발생시킨다.
     *
     * @param campaign 캠페인 엔티티
     * @param brand    브랜드 엔티티
     * @throws NotCampaignOwnershipException 캠페인 작성자가 브랜드가 아닌 경우 발생하는 예외
     */
    private static void validateBrandOwnsCampaign(Campaign campaign, Brand brand) {
        if (!campaign.getBrand().getId().equals(brand.getId())) {
            throw new NotCampaignOwnershipException();
        }
    }
}
