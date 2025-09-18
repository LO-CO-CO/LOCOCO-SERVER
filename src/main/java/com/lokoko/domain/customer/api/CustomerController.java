package com.lokoko.domain.customer.api;

import com.lokoko.domain.customer.api.dto.request.CustomerMyPageRequest;
import com.lokoko.domain.customer.api.dto.request.CustomerProfileImageRequest;
import com.lokoko.domain.customer.api.dto.response.CustomerMyPageResponse;
import com.lokoko.domain.customer.api.dto.response.CustomerProfileImageResponse;
import com.lokoko.domain.customer.api.dto.response.CustomerSnsConnectedResponse;
import com.lokoko.domain.customer.api.message.ResponseMessage;
import com.lokoko.domain.customer.application.CustomerService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "CUSTOMER")
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping("/profile/image")
    @Operation(summary = "Customer profile image presignedUrl 발급")
    public ApiResponse<CustomerProfileImageResponse> createProfileImagePresignedUrl(
            @Parameter(hidden = true) @CurrentUser Long customerId,
            @Valid @RequestBody CustomerProfileImageRequest request) {

        CustomerProfileImageResponse response = customerService.createCustomerProfilePresignedUrl(customerId, request);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CUSTOMER_PROFILE_IMAGE_PRESIGNED_URL_SUCCESS.getMessage(), response);
    }


    @Operation(summary = "Customer 마이페이지(프로필) 정보 조회")
    @GetMapping("/profile")
    public ApiResponse<CustomerMyPageResponse> getCustomerMyPageInfo(@Parameter(hidden = true) @CurrentUser Long customerId) {

        CustomerMyPageResponse response = customerService.getCustomerMyPage(customerId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CUSTOMER_GET_MYPAGE_INFO_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "Customer 마이페이지(프로필) 정보 수정")
    @PatchMapping("/profile")
    public ApiResponse<Void> updateCustomerMyPageProfile(
            @Parameter(hidden = true) @CurrentUser Long customerId,
            @Valid @RequestBody CustomerMyPageRequest request) {

        customerService.updateCustomerMyPage(customerId, request);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CUSTOMER_UPDATE_MYPAGE_INFO_SUCCESS.getMessage());
    }

    @GetMapping("/sns-status")
    @Operation(summary = "Customer SNS 연동 여부를 체크하는 API입니다")
    public ApiResponse<CustomerSnsConnectedResponse> getCustomerSnsConnected(
            @Parameter(hidden = true) @CurrentUser Long customer) {

        CustomerSnsConnectedResponse response = customerService.getCustomerSnsStatus(customer);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CUSTOMER_GET_SNS_STATUS_SUCCESS.getMessage(), response);
    }

}