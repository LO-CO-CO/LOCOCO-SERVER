package com.lokoko.domain.productBrand.domain.repository;

import com.lokoko.domain.productBrand.domain.entity.ProductBrand;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductBrandRepository extends JpaRepository<ProductBrand, Long> {
    List<ProductBrand> findByBrandNameStartingWithIgnoreCase(String prefix, Sort sort);

    @Query(value = "SELECT * FROM product_brand pb WHERE pb.brand_name REGEXP '^[0-9]' ORDER BY pb.brand_name ASC", nativeQuery = true)
    List<ProductBrand> findAllStartsWithDigit();
}