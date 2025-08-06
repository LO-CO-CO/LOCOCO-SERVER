package com.lokoko.domain.like.domain.repository;

import com.lokoko.domain.like.domain.entity.ProductLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    Optional<ProductLike> findByProductIdAndUserId(Long productId, Long userId);

    long countByProductId(Long postId);

    List<ProductLike> findAllByUserId(Long userId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    @Query("SELECT l.product.id FROM ProductLike l where l.user.id =:userId AND l.product.id IN :productIds")
    List<Long> findLikedProductIds(Long userId, List<Long> productIds);
}
