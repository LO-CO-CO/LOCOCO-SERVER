package com.lokoko.domain.creator.application.service;

import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.creator.api.dto.request.CreatorInfoUpdateRequest;
import com.lokoko.domain.creator.api.dto.request.CreatorMyPageUpdateRequest;
import com.lokoko.domain.creator.api.dto.request.CreatorProfileImageRequest;
import com.lokoko.domain.creator.api.dto.request.CreatorSnsLinkRequest;
import com.lokoko.domain.creator.api.dto.response.*;
import com.lokoko.domain.creator.application.mapper.CreatorMapper;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.exception.CreatorInfoNotCompletedException;
import com.lokoko.domain.creator.exception.NotCreatorRoleException;
import com.lokoko.domain.creator.exception.SnsNotConnectedException;
import com.lokoko.domain.creatorCampaign.application.mapper.CreatorCampaignMapper;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.user.application.service.UserGetService;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatorUsecase {

    private final UserGetService userGetService;
    private final CreatorGetService creatorGetService;
    private final CreatorCampaignGetService creatorCampaignGetService;
    private final CampaignReviewGetService campaignReviewGetService;

    private final CreatorUpdateService creatorUpdateService;

    private final AuthService authService;

    private final CreatorMapper creatorMapper;
    private final CreatorCampaignMapper creatorCampaignMapper;

    @Transactional(readOnly = true)
    public CreatorMyPageResponse getMyProfile(Long userId) {
        Creator creator = creatorGetService.findByUserId(userId);

        return creatorMapper.toMyPageResponse(creator);
    }

    @Transactional(readOnly = true)
    public CreatorMyCampaignListResponse getMyCampaigns(Long userId, int page, int size) {
        Creator creator = creatorGetService.findByUserId(userId);

        Slice<CreatorCampaign> slice =
                creatorCampaignGetService.findMyCampaigns(creator.getId(), page, size);

        Long totalElements = creatorCampaignGetService.countMyCampaigns(creator.getId());

        List<CreatorMyCampaignResponse> campaigns = slice.getContent().stream()
                .map(creatorCampaignMapper::toMyCampaignResponse)
                .toList();

        return creatorCampaignMapper.toMyCampaignListResponse(creator, campaigns, slice, totalElements);
    }

    @Transactional
    public CreatorMyPageResponse updateMyProfile(Long userId, CreatorMyPageUpdateRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        Creator updated = creatorUpdateService.updateProfile(creator, request);

        return creatorMapper.toMyPageResponse(updated);
    }

    @Transactional
    public CreatorProfileImageResponse createPresignedUrlForProfile(Long userId,
                                                                    CreatorProfileImageRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);

        return creatorUpdateService.createPresignedUrlForProfile(creator.getId(), request);
    }

    @Transactional(readOnly = true)
    public CreatorAddressInfo getMyAddress(Long userId) {
        Creator creator = creatorGetService.findByUserId(userId);
        return creatorMapper.toAddressInfo(creator);
    }

    @Transactional
    public void confirmAddress(Long userId, Long campaignId) {
        Creator creator = creatorGetService.findByUserId(userId);

        creatorUpdateService.confirmAddress(campaignId, creator.getId());
    }

    // 회원 가입
    @Transactional
    public void updateCreatorRegisterInfo(Long userId, CreatorInfoUpdateRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        creatorUpdateService.updateRegisterCreatorInfo(creator, request);
    }

    @Transactional(readOnly = true)
    public CreatorSnsConnectedResponse getCreatorSnsStatus(Long userId) {
        Creator creator = creatorGetService.findByUserId(userId);
        return creatorMapper.toSnsStateResponse(creator);
    }

    @Transactional(readOnly = true)
    public CreatorSnsLinkResponse getCreatorSnsUrls(Long userId) {
        Creator creator = creatorGetService.findByUserId(userId);
        return creatorMapper.toSnsLinkResponse(creator);
    }

    @Transactional(readOnly = true)
    public CreatorInfoResponse getRegisterInfo(Long userId) {
        Creator creator = creatorGetService.findByUserId(userId);
        return creatorMapper.toRegisterInfoResponse(creator);
    }

    @Transactional(readOnly = true)
    public CreatorRegisterCompleteResponse completeCreatorSignup(Long userId) {
        User user = userGetService.findUserById(userId);

        if (user.getRole() != Role.CREATOR) {
            throw new NotCreatorRoleException();
        }

        Creator creator = creatorGetService.findByUserId(userId);

        OauthLoginStatus currentStatus = authService.getCreatorStatus(user);

        if (currentStatus != OauthLoginStatus.SNS_REQUIRED) {
            if (currentStatus == OauthLoginStatus.INFO_REQUIRED) {
                throw new CreatorInfoNotCompletedException();
            }
        }

        boolean hasInstagram = creator.getInstagramUserId() != null && !creator.getInstagramUserId().isBlank();
        boolean hasTiktok = creator.getTikTokUserId() != null && !creator.getTikTokUserId().isBlank();

        if (!hasInstagram && !hasTiktok) {
            throw new SnsNotConnectedException();
        }

        return new CreatorRegisterCompleteResponse(OauthLoginStatus.LOGIN);
    }

    @Transactional
    public CreatorSnsLinkResponse updateCreatorSnsLink(Long userId, CreatorSnsLinkRequest request) {
        User user = userGetService.findUserById(userId);

        if (user.getRole() != Role.CREATOR) {
            throw new NotCreatorRoleException();
        }

        Creator creator = creatorGetService.findByUserId(userId);

        OauthLoginStatus currentStatus = authService.getCreatorStatus(user);

        if (currentStatus == OauthLoginStatus.INFO_REQUIRED) {
            throw new CreatorInfoNotCompletedException();
        }

        creatorUpdateService.updateSnsLink(creator, request);

        return creatorMapper.toSnsLinkResponse(creator);
    }


}
