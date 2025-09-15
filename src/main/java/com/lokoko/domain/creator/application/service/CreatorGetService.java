package com.lokoko.domain.creator.application.service;

import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import com.lokoko.domain.creator.exception.CreatorCampaignNotFoundException;
import com.lokoko.domain.creator.exception.CreatorNotFoundException;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreatorGetService {

    private final CreatorRepository creatorRepository;
    private final CreatorCampaignRepository creatorCampaignRepository;
    private final UserRepository userRepository;

    public Creator findByUserId(Long userId) {
        return creatorRepository.findByUserId(userId)
                .orElseThrow(CreatorNotFoundException::new);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public CreatorCampaign findParticipation(Long campaignId, Long creatorId) {

        return creatorCampaignRepository
                .findByCampaign_IdAndCreator_Id(campaignId, creatorId)
                .orElseThrow(CreatorCampaignNotFoundException::new);
    }

    public boolean isCreatorNameAvailable(String creatorName, Long currentCreatorId) {
        Creator currentCreator = creatorRepository.findById(currentCreatorId).orElse(null);

        // 자신의 현재 이름과 같으면 사용 가능
        if (currentCreator != null &&
                currentCreator.getCreatorName() != null &&
                currentCreator.getCreatorName().equalsIgnoreCase(creatorName)) {
            return true;
        }

        return !creatorRepository.existsByCreatorName(creatorName);
    }
}

