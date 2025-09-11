package com.lokoko.domain.creator.application.service;


import com.lokoko.domain.campaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.campaignReview.application.service.CreatorCampaignUpdateService;
import com.lokoko.domain.creator.api.dto.request.CreatorMyPageUpdateRequest;
import com.lokoko.domain.creator.domain.entity.Creator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreatorUpdateService {

    private final CreatorGetService creatorGetService;
    private final CreatorCampaignUpdateService creatorCampaignUpdateService;

    /**
     * 마이페이지 수정 - null 필드는 무시(부분 업데이트) - 유효성은 Request DTO(@NotBlank/@Size 등)에서 선검증
     */
    public Creator updateProfile(Creator creator, CreatorMyPageUpdateRequest request) {

        if (request.creatorName() != null) {
            creator.changeCreatorName(request.creatorName());
        }
        if (request.firstName() != null) {
            creator.changeFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            creator.changeLastName(request.lastName());
        }

        if (request.countryCode() != null) {
            creator.changeCountryCode(request.countryCode());
        }
        if (request.phoneNumber() != null) {
            creator.changePhoneNumber(request.phoneNumber());
        }

        if (request.country() != null) {
            creator.changeCountry(request.country());
        }
        if (request.stateOrProvince() != null) {
            creator.changeStateOrProvince(request.stateOrProvince());
        }
        if (request.cityOrTown() != null) {
            creator.changeCityOrTown(request.cityOrTown());
        }
        if (request.addressLine1() != null) {
            creator.changeAddressLine1(request.addressLine1());
        }
        if (request.addressLine2() != null) {
            creator.changeAddressLine2(request.addressLine2());
        }
        if (request.postalCode() != null) {
            creator.changePostalCode(request.postalCode());
        }

        if (request.skinType() != null) {
            creator.changeSkinType(request.skinType());
        }
        if (request.skinTone() != null) {
            creator.changeSkinTone(request.skinTone());
        }

        return creator;
    }

    /**
     * 배송지 확정 - 참여 레코드 조회 → 주소 확정 플래그/시간 변경 - 참여 상태는 공통 로직으로 재계산(주소만 확정이면 APPROVED_ADDRESS_CONFIRMED)
     */
    public void confirmAddress(Long campaignId, Long creatorId) {
        CreatorCampaign participation = creatorGetService.findParticipation(campaignId, creatorId);

        participation.changeAddressConfirmed(true);

        creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());
    }
}
