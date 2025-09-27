package com.lokoko.domain.user.application.usecase;

import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.user.application.service.AdminCampaignUpdateService;
import com.lokoko.domain.user.application.service.AdminReviewDeleteService;
import com.lokoko.domain.user.application.service.UserGetService;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.global.utils.AdminValidator;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUsecase {

    private final UserGetService userGetService;
    private final CampaignGetService campaignGetService;

    private final AdminCampaignUpdateService adminCampaignUpdateService;
    private final AdminReviewDeleteService adminReviewDeleteService;

    @Transactional
    public void approveCampaign(Long userId, Long campaignId) {
        User user = userGetService.findUserById(userId);
        AdminValidator.validateUserRole(user);

        campaignGetService.findByCampaignId(campaignId);
        adminCampaignUpdateService.approve(campaignId, Instant.now());
    }

    @Transactional
    public void deleteReviewByAdmin(Long UserId, Long campaignReviewId) {
        User user = userGetService.findUserById(UserId);
        AdminValidator.validateUserRole(user);

        adminReviewDeleteService.deleteReview(UserId, campaignReviewId);
    }
}
