package com.lokoko.domain.image.domain.repository;

import com.lokoko.domain.image.domain.entity.ProductImage;
import com.lokoko.domain.product.domain.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdIn(List<Long> productIds);

    List<ProductImage> findByProductIdInAndIsMainTrue(List<Long> productIds);

    Optional<ProductImage> findByProduct(Product product);

    Optional<ProductImage> findByProductAndIsMainTrue(Product product);

}
