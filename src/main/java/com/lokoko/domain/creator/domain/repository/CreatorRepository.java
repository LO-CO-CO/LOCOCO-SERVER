package com.lokoko.domain.creator.domain.repository;

import com.lokoko.domain.creator.domain.entity.Creator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {

    @Query("select c from Creator c where c.user.id = :userId")
    Optional<Creator> findByUserId(@Param("userId") Long userId);
}