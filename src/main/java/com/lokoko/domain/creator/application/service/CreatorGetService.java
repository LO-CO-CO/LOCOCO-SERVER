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
                .findByCampaignIdAndCreatorId(campaignId, creatorId)
                .orElseThrow(CreatorCampaignNotFoundException::new);
    }

}

