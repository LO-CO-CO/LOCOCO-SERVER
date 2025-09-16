package com.lokoko.domain.creator.domain.repository;

import com.lokoko.domain.creator.domain.entity.Creator;
import java.util.Optional;

import com.lokoko.domain.creator.exception.CreatorNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {

    Optional<Creator> findByUserId(Long userId);

    default Creator findByIdOrThrow(Long creatorId) {
        return findById(creatorId)
                .orElseThrow(CreatorNotFoundException::new);
    }
    boolean existsByCreatorNameIgnoreCase(String creatorName);
}