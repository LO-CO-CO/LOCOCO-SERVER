package com.lokoko.domain.creator.domain.repository;

import com.lokoko.domain.creator.domain.entity.Creator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {
    boolean existsByCreatorName(String creatorName);

    Optional<Creator> findByUserId(Long userId);

    boolean existsByCreatorNameIgnoreCase(String creatorName);

}