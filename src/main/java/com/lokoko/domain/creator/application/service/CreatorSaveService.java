package com.lokoko.domain.creator.application.service;

import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatorSaveService {

    private final CreatorRepository creatorRepository;

    @Transactional
    public Creator save(Creator creator) {
        return creatorRepository.save(creator);
    }
}
