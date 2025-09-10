package com.lokoko.domain.creator.application.service;

import com.lokoko.domain.campaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.campaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.creator.api.dto.response.CreatorMyPageResponse;
import com.lokoko.domain.creator.application.mapper.CreatorMapper;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import com.lokoko.domain.creator.exception.CreatorCampaignNotFoundException;
import com.lokoko.domain.creator.exception.CreatorNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreatorGetService {

    private final CreatorRepository creatorRepository;
    private final CreatorCampaignRepository creatorCampaignRepository;
    private final CreatorMapper creatorMapper;

    public Creator findByUserId(Long userId) {
        return creatorRepository.findByUserId(userId)
                .orElseThrow(CreatorNotFoundException::new);
    }

    public CreatorCampaign findParticipation(Long campaignId, Long creatorId) {
        return creatorCampaignRepository.findByCreatorIdAndCampaignId(campaignId, creatorId)
                .orElseThrow(CreatorCampaignNotFoundException::new);
    }

    public CreatorMyPageResponse getMyProfile(Long userId) {
        Creator creator = findByUserId(userId);
        return creatorMapper.toMyPageResponse(creator);
    }
}
