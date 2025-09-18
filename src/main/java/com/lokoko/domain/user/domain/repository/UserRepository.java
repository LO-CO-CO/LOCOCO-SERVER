package com.lokoko.domain.user.domain.repository;

import com.lokoko.domain.user.domain.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGoogleId(String googleId);

    Optional<User> findByLineId(String lineId);

    @Query("""
                SELECT COALESCE(c.creatorName, cu.customerName)
                FROM User u
                LEFT JOIN Creator c ON u.id = c.id
                LEFT JOIN Customer cu ON u.id = cu.id
                WHERE u.id = :userId
            """)
    String findCurrentUserName(@Param("userId") Long userId);
}
