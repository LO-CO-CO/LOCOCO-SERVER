package com.lokoko.domain.user.application.service;

import com.lokoko.domain.creator.application.service.CreatorGetService;
import com.lokoko.domain.creator.application.service.CreatorSaveService;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.entity.enums.CreatorStatus;
import com.lokoko.domain.creator.exception.NotCreatorRoleException;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.entity.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCreatorUpdateService {

    private final CreatorGetService creatorGetService;

    private final CreatorSaveService creatorSaveService;
    private final UserGetService userGetService;
    private final UserSaveService userSaveService;

    @Transactional
    public void approveById(Long userId, Instant now) {
        Creator creator = creatorGetService.findByUserId(userId);

        if (creator.getCreatorStatus() == CreatorStatus.APPROVED) {
            return;
        }

        creator.approve(now);
        creatorSaveService.save(creator);
    }


    @Transactional
    public void approveCreators(List<Long> creatorIds, Instant now) {
        creatorIds.stream()
                .distinct()
                .forEach(id -> {
                    Creator creator = creatorGetService.findByUserId(id);

                    // 이미 승인된 경우는 패스
                    if (creator.getCreatorStatus() == CreatorStatus.APPROVED) {
                        return;
                    }

                    // NOT_APPROVED → APPROVED
                    creator.approve(now);
                    creatorSaveService.save(creator);
                });
    }

    @Transactional
    public void deleteCreators(List<Long> creatorIds) {

        creatorIds.stream()
                .distinct()
                .forEach(id -> {
                    User user = userGetService.findUserById(id);

                    if (user.getRole() != Role.CREATOR) {
                        throw new NotCreatorRoleException();
                    }

                    // 이미 비활성화면 패스
                    if (user.getStatus() == UserStatus.INACTIVE) {
                        return;
                    }

                    user.updateStatus(UserStatus.INACTIVE);
                    userSaveService.save(user);
                });
    }
}
