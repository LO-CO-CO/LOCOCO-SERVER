package com.lokoko.domain.creator.application.mapper;

import com.lokoko.domain.creator.api.dto.response.CreatorAddressInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorBasicInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorContactInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorFaceInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorInfoResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorMyPageResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorSnsConnectedResponse;
import com.lokoko.domain.creator.domain.entity.Creator;
import org.springframework.stereotype.Component;

@Component
public class CreatorMapper {

    public CreatorMyPageResponse toMyPageResponse(Creator creator) {
        return CreatorMyPageResponse.builder()
                .creatorId(creator.getId())
                .creatorBasicInfo(CreatorBasicInfo.builder()
                        .profileImageUrl(creator.getUser().getProfileImageUrl())
                        .creatorName(creator.getCreatorName())
                        .firstName(creator.getFirstName())
                        .lastName(creator.getLastName())
                        .gender(creator.getGender())
                        .birthDate(creator.getBirthDate())
                        .build())
                .creatorContactInfo(CreatorContactInfo.builder()
                        .email(creator.getUser().getEmail())
                        .countryCode(creator.getCountryCode())
                        .phoneNumber(creator.getPhoneNumber())
                        .build())
                .creatorAddressInfo(CreatorAddressInfo.builder()
                        .country(creator.getCountry())
                        .stateOrProvince(creator.getStateOrProvince())
                        .cityOrTown(creator.getCityOrTown())
                        .addressLine1(creator.getAddressLine1())
                        .addressLine2(creator.getAddressLine2())
                        .postalCode(creator.getPostalCode())
                        .build())
                .creatorFaceInfo(CreatorFaceInfo.builder()
                        .skinType(creator.getSkinType())
                        .skinTone(creator.getSkinTone())
                        .build())
                .creatorType(creator.getCreatorType())
                .creatorStatus(creator.getCreatorStatus())
                .contentLanguage(creator.getContentLanguage())
                .build();
    }


    public CreatorInfoResponse toRegisterInfoResponse(Creator creator) {
        return new CreatorInfoResponse(
                creator.getCreatorName(),
                creator.getBirthDate(),
                creator.getGender(),
                creator.getFirstName(),
                creator.getLastName(),
                creator.getCountryCode(),
                creator.getPhoneNumber(),
                creator.getContentLanguage(),
                creator.getCountry(),
                creator.getStateOrProvince(),
                creator.getCityOrTown(),
                creator.getAddressLine1(),
                creator.getAddressLine2(),
                creator.getPostalCode(),
                creator.getSkinType(),
                creator.getSkinTone()
        );
    }

    public CreatorSnsConnectedResponse toSnsStateResponse(Creator creator) {
        return new CreatorSnsConnectedResponse(
                creator.getInstaLink() != null,
                creator.getTiktokLink() != null
        );
    }

    public CreatorAddressInfo toAddressInfo(Creator creator) {
        return CreatorAddressInfo.builder()
                .country(creator.getCountry())
                .stateOrProvince(creator.getStateOrProvince())
                .cityOrTown(creator.getCityOrTown())
                .addressLine1(creator.getAddressLine1())
                // null 가능
                .addressLine2(creator.getAddressLine2())
                .postalCode(creator.getPostalCode())
                .build();
    }
}
