package com.lokoko.domain.customer.api.dto.response;

import com.lokoko.domain.creator.domain.entity.enums.ContentLanguage;
import com.lokoko.domain.creator.domain.entity.enums.Gender;
import com.lokoko.domain.creator.domain.entity.enums.SkinTone;
import com.lokoko.domain.creator.domain.entity.enums.SkinType;
import com.lokoko.domain.customer.domain.entity.Customer;
import com.lokoko.domain.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CustomerMyPageResponse(

        @Schema(description = "Customer 프로필 이미지 url", example = "https://s3.example.com/profile/us-user-1001.jpg")
        String profileImageUrl,

        @Schema(requiredMode = REQUIRED, description = "구글 로그인시 받은 email", example = "lococo@example.com")
        String email,

        @Schema(requiredMode = REQUIRED, description = "이름", example = "Jessica")
        String firstName,

        @Schema(requiredMode = REQUIRED, description = "성", example = "Anderson")
        String lastName,

        @Schema(description = "customer id", example = "hyoeun")
        String userName,

        @Schema(description = "생년월일", example = "2002-08-21")
        LocalDate birthDate,

        @Schema(description = "성별", example = "MALE")
        Gender gender,

        @Schema(description = "국가번호 (선택, 최대 5자)", example = "+1")
        String countryCode,

        @Schema(description = "전화번호 (선택, 최대 20자)", example = "01012345678")
        String phoneNumber,

        @Schema(description = "콘텐츠 언어", example = "ENGLISH")
        ContentLanguage contentLanguage,

        @Schema(description = "국가", example = "US")
        String country,

        @Schema(description = "State (텍스트 최대 20자)", example = "CA")
        String stateOrProvince,

        @Schema(description = "City/Town (텍스트, 최대 20자)", example = "San Francisco")
        String cityOrTown,

        @Schema(description = "Address Line 1 (최대 30자)", example = "1234 Market St")
        String addressLine1,

        @Schema(description = "Address Line 2 (최대 30자)", example = "Apt 5B")
        String addressLine2,

        @Schema(description = "ZIP Code (최대 10자)", example = "94103")
        String postalCode,

        @Schema(description = "피부 타입 (드롭다운 6개)", example = "COMBINATION")
        SkinType skinType,

        @Schema(description = "피부 톤 (드롭다운 20개)", example = "SHADE_12")
        SkinTone skinTone

) {

    public static CustomerMyPageResponse toMyPage(Customer customer
    ) {
        User user = customer.getUser();

        return new CustomerMyPageResponse(
                user.getProfileImageUrl(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                customer.getCustomerName(),
                customer.getBirthDate(),
                customer.getGender(),
                customer.getCountryCode(),
                customer.getPhoneNumber(),
                customer.getContentLanguage(),
                customer.getCountry(),
                customer.getStateOrProvince(),
                customer.getCityOrTown(),
                customer.getAddressLine1(),
                customer.getAddressLine2(),
                customer.getPostalCode(),
                customer.getSkinType(),
                customer.getSkinTone()
        );
    }
}