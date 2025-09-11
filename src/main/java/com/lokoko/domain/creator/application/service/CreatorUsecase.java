package com.lokoko.domain.creator.application.service;

import com.lokoko.domain.creator.api.dto.request.CreatorMyPageUpdateRequest;
import com.lokoko.domain.creator.api.dto.response.CreatorMyPageResponse;
import com.lokoko.domain.creator.application.mapper.CreatorMapper;
import com.lokoko.domain.creator.domain.entity.Creator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatorUsecase {

    private final CreatorGetService creatorGetService;
    private final CreatorUpdateService creatorUpdateService;

    private final CreatorMapper creatorMapper;

    @Transactional(readOnly = true)
    public CreatorMyPageResponse getMyProfile(Long userId) {
        Creator creator = creatorGetService.findByUserId(userId);

        return creatorMapper.toMyPageResponse(creator);
    }

    @Transactional
    public CreatorMyPageResponse updateMyProfile(Long userId, CreatorMyPageUpdateRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        Creator updated = creatorUpdateService.updateProfile(creator, request);

        return creatorMapper.toMyPageResponse(updated);
    }

    @Transactional
    public void confirmAddress(Long userId, Long campaignId) {
        Creator creator = creatorGetService.findByUserId(userId);

        creatorUpdateService.confirmAddress(campaignId, creator.getId());
    }
}
