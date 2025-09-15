package com.lokoko.domain.creator.domain.repository;

import com.lokoko.domain.creator.domain.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {
    boolean existsByCreatorName(String creatorName);
}