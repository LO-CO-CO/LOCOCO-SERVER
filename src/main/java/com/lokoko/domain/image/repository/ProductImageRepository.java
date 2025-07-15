package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.product.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdIn(List<Long> productIds);

    Optional<ProductImage> findByProduct(Product product);
}
