package com.lokoko.domain.user.application.service;

import com.lokoko.domain.creator.application.service.CreatorGetService;
import com.lokoko.domain.creator.application.service.CreatorSaveService;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.entity.enums.CreatorStatus;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCreatorUpdateService {

    private final CreatorGetService creatorGetService;

    private final CreatorSaveService creatorSaveService;

    @Transactional
    public void approveById(Long userId, Instant now) {
        Creator creator = creatorGetService.findByUserId(userId);

        if (creator.getCreatorStatus() == CreatorStatus.APPROVED) {
            return;
        }

        creator.approve(now);
        creatorSaveService.save(creator);
    }
}
