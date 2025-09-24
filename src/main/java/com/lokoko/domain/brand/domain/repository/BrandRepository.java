package com.lokoko.domain.brand.domain.repository;

import com.lokoko.domain.brand.domain.entity.Brand;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query("SELECT b FROM Brand b JOIN FETCH b.user WHERE b.id = :brandId")
    Optional<Brand> findBrandWithUserById(@Param("brandId") Long brandId);

}