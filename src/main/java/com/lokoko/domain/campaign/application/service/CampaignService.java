package com.lokoko.domain.campaign.application.service;

import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.brand.domain.repository.BrandRepository;
import com.lokoko.domain.brand.exception.BrandNotFoundException;
import com.lokoko.domain.campaign.api.dto.request.CampaignCreateRequest;
import com.lokoko.domain.campaign.api.dto.request.CampaignDraftRequest;
import com.lokoko.domain.campaign.api.dto.request.CampaignPublishRequest;
import com.lokoko.domain.campaign.api.dto.request.CampaignMediaRequest;
import com.lokoko.domain.campaign.api.dto.response.CampaignCreateResponse;
import com.lokoko.domain.campaign.api.dto.response.CampaignImageResponse;
import com.lokoko.domain.campaign.api.dto.response.CampaignMediaResponse;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.ActionType;
import com.lokoko.domain.campaign.domain.repository.CampaignRepository;
import com.lokoko.domain.campaign.exception.CampaignNotEditableException;
import com.lokoko.domain.campaign.exception.CampaignNotFoundException;
import com.lokoko.domain.campaign.exception.DraftNotFilledException;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.image.domain.entity.CampaignImage;
import com.lokoko.domain.image.domain.entity.enums.ImageType;
import com.lokoko.domain.image.domain.repository.CampaignImageRepository;
import com.lokoko.domain.review.exception.ErrorMessage;
import com.lokoko.domain.review.exception.InvalidMediaTypeException;
import com.lokoko.global.common.dto.PresignedUrlResponse;
import com.lokoko.global.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.lokoko.global.utils.AllowedMediaType.ALLOWED_MEDIA_TYPES;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignImageRepository campaignImageRepository;
    private final BrandRepository brandRepository;
    private final S3Service s3Service;

    public CampaignMediaResponse createMediaPresignedUrl(Long brandId, CampaignMediaRequest request) {

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(BrandNotFoundException::new);

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
    public CampaignCreateResponse createCampaignWithAction(Long brandId, ActionType actionType, CampaignCreateRequest createRequest) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(BrandNotFoundException::new);

        Campaign campaign = Campaign.createCampaign(createRequest, brand);

        if (actionType == ActionType.PUBLISH) {
            if(campaign.isDraft()) throw new DraftNotFilledException();
            campaign.publish();
        }

        Campaign savedCampaign = campaignRepository.save(campaign);
        List<CampaignImage> savedImages = saveImages(createRequest, savedCampaign);

        return buildCampaignCreateResponse(savedCampaign, savedImages);
    }

    private List<CampaignImage> saveImages(CampaignCreateRequest createRequest, Campaign campaign) {

        List<CampaignImage> toSaveImages = new ArrayList<>();

        createRequest.topImages()
                .forEach(img -> toSaveImages.add(CampaignImage.createCampaignImage(
                        img.url(), img.displayOrder(), img.imageType(), campaign)));

        createRequest.bottomImages()
                .forEach(img -> toSaveImages.add(CampaignImage.createCampaignImage(
                        img.url(), img.displayOrder(), img.imageType(), campaign)));

        return campaignImageRepository.saveAll(toSaveImages);
    }

    private CampaignCreateResponse buildCampaignCreateResponse(
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

        return CampaignCreateResponse.of(campaign, topImages, bottomImages);

    }

    @Transactional
    public CampaignCreateResponse updateCampaign(Long brandId, Long campaignId, ActionType actionType, CampaignCreateRequest updateRequest) {

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(BrandNotFoundException::new);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        validateBrandOwnsCampaign(campaign, brand);

        validateEditableCampaign(campaign);

        campaign.updateCampaign(updateRequest);

        if (actionType == ActionType.PUBLISH) {
            if (campaign.isDraft()) throw new DraftNotFilledException();
            campaign.publish();
        }

        // 기존에 존재하는 이미지 삭제후 갈아끼우기
        campaignImageRepository.deleteByCampaignId(campaignId);
        List<CampaignImage> savedImages = saveImages(updateRequest, campaign);

        return buildCampaignCreateResponse(campaign, savedImages);
    }

    private void validateEditableCampaign(Campaign campaign) {
        if (campaign.isPublished()) {
            throw new CampaignNotEditableException();
        }
    }

    private static void validateBrandOwnsCampaign(Campaign campaign, Brand brand) {
        if (!campaign.getBrand().getId().equals(brand.getId())) {
            throw new NotCampaignOwnershipException();
        }
    }

    @Transactional
    public CampaignCreateResponse createCampaignDraft(Long brandId, CampaignDraftRequest draftRequest) {
        CampaignCreateRequest createRequest = CampaignCreateRequest.convertDraftToCreateRequest(draftRequest);
        return createCampaignWithAction(brandId, ActionType.SAVE_DRAFT, createRequest);
    }

    @Transactional
    public CampaignCreateResponse createAndPublishCampaign(Long brandId, CampaignPublishRequest publishRequest) {
        CampaignCreateRequest createRequest = CampaignCreateRequest.convertPublishToCreateRequest(publishRequest);
        return createCampaignWithAction(brandId, ActionType.PUBLISH, createRequest);
    }

    @Transactional
    public CampaignCreateResponse updateCampaignToDraft(Long brandId, Long campaignId, CampaignDraftRequest draftRequest) {
        CampaignCreateRequest updateRequest = CampaignCreateRequest.convertDraftToCreateRequest(draftRequest);
        return updateCampaign(brandId, campaignId, ActionType.SAVE_DRAFT, updateRequest);
    }

    @Transactional
    public CampaignCreateResponse updateAndPublishCampaign(Long brandId, Long campaignId, CampaignPublishRequest publishRequest) {
        CampaignCreateRequest updateRequest = CampaignCreateRequest.convertPublishToCreateRequest(publishRequest);
        return updateCampaign(brandId, campaignId, ActionType.PUBLISH, updateRequest);
    }

}
