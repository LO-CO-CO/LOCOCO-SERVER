package com.lokoko.domain.creator.application.mapper;

import com.lokoko.domain.creator.api.dto.response.CreatorAddressInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorBasicInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorContactInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorFaceInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorMyPageResponse;
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
}
