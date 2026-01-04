package com.lokoko.domain.user.application.service;

import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.user.api.dto.response.AdminCreatorListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCreatorGetService {

    private final CreatorCampaignRepository creatorCampaignRepository;

    public AdminCreatorListResponse findAllCreators(int page, int size) {
        return creatorCampaignRepository.findAdminCreators(PageRequest.of(page, size));
    }
}
