package com.lokoko.domain.creator.application.service;

import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import com.lokoko.domain.creator.exception.CreatorNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreatorGetService {

    private final CreatorRepository creatorRepository;

    public Creator findByUserId(Long userId) {
        return creatorRepository.findByUserId(userId)
                .orElseThrow(CreatorNotFoundException::new);
    }
}

