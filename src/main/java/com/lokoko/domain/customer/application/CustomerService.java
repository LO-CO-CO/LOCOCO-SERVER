package com.lokoko.domain.customer.application;


import com.lokoko.domain.customer.api.dto.request.CustomerMyPageRequest;
import com.lokoko.domain.customer.api.dto.request.CustomerProfileImageRequest;
import com.lokoko.domain.customer.api.dto.response.CustomerMyPageResponse;
import com.lokoko.domain.customer.api.dto.response.CustomerProfileImageResponse;
import com.lokoko.domain.customer.api.dto.response.CustomerSnsConnectedResponse;
import com.lokoko.domain.customer.domain.entity.Customer;
import com.lokoko.domain.customer.domain.repository.CustomerRepository;
import com.lokoko.domain.customer.exception.CustomerNotFoundException;
import com.lokoko.domain.productReview.exception.ErrorMessage;
import com.lokoko.domain.productReview.exception.InvalidMediaTypeException;
import com.lokoko.domain.user.application.service.UserService;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.global.common.entity.MediaFile;
import com.lokoko.global.common.service.S3Service;
import com.lokoko.global.utils.S3UrlParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final S3Service s3Service;
    private final UserService userService;

    public CustomerProfileImageResponse createCustomerProfilePresignedUrl(Long customerId, CustomerProfileImageRequest request) {
        customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);

        String mediaType = request.mediaType();
        if (mediaType == null || mediaType.isBlank() || !mediaType.startsWith("image/")) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }

        String presignedUrl = s3Service.generatePresignedUrl(mediaType).presignedUrl();

        return new CustomerProfileImageResponse(presignedUrl);

    }

    public CustomerMyPageResponse getCustomerMyPage(Long customerId) {
        Customer customer = customerRepository.findCustomerWithUserById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        return CustomerMyPageResponse.toMyPage(customer);
    }

    @Transactional
    public void updateCustomerMyPage(Long customerId, CustomerMyPageRequest request) {
        Customer customer = customerRepository.findCustomerWithUserById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        User user = customer.getUser();

        if (request.profileImageUrl() != null) {
            MediaFile mediaFile = S3UrlParser.parsePresignedUrl(request.profileImageUrl());
            user.updateProfileImage(mediaFile.getFileUrl());
        }

        if (request.customerName() != null) {
            userService.checkUserIdAvailable(request.customerName(), customerId);
            customer.assignCustomerName(request.customerName());
        }

        if (request.birthDate() != null) {
            customer.assignBirthDate(request.birthDate());
        }

        if (request.gender() != null) {
            customer.assignGender(request.gender());
        }

        if (request.firstName() != null) {
            user.updateFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.updateLastName(request.lastName());
        }

        if (request.countryCode() != null) {
            customer.assignCountryCode(request.countryCode());
        }

        if (request.phoneNumber() != null) {
            customer.assignPhoneNumber(request.phoneNumber());
        }

        if (request.contentLanguage() != null) {
            customer.assignContentLanguage(request.contentLanguage());
        }

        if (request.country() != null) {
            customer.assignCountry(request.country());
        }

        if (request.stateOrProvince() != null) {
            customer.assignStateOrProvince(request.stateOrProvince());
        }

        if (request.cityOrTown() != null) {
            customer.assignCityOrTown(request.cityOrTown());
        }

        if (request.addressLine1() != null) {
            customer.assignAddressLine1(request.addressLine1());
        }

        if (request.addressLine2() != null) {
            customer.assignAddressLine2(request.addressLine2());
        }

        if (request.postalCode() != null) {
            customer.assignPostalCode(request.postalCode());
        }

        if (request.skinType() != null) {
            customer.assignSkinType(request.skinType());
        }

        if (request.skinTone() != null) {
            customer.assignSkinTone(request.skinTone());
        }
    }

    public CustomerSnsConnectedResponse getCustomerSnsStatus(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        return new CustomerSnsConnectedResponse(
                customer.getInstaUserId() != null,
                customer.getTikTokUserId() != null
        );
    }

}
