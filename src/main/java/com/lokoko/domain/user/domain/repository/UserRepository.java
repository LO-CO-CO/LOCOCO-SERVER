package com.lokoko.domain.user.domain.repository;

import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.exception.CreatorNotFoundException;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.exception.UserNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByLineId(String lineId);

    default User findByIdOrThrow(Long creatorId) {
        return findById(creatorId)
                .orElseThrow(UserNotFoundException::new);
    }

}
