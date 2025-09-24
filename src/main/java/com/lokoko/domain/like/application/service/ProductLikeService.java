package com.lokoko.domain.like.application.service;

import com.lokoko.domain.like.domain.entity.ProductLike;
import com.lokoko.domain.like.domain.repository.ProductLikeRepository;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.global.common.annotation.DistributedLock;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductLikeService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductLikeRepository productLikeRepository;

    @DistributedLock(key = "'like:product:' + #productId + ':user:' + #userId")
    public void toggleProductLike(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        processProductLike(product, user);
    }

    public void processProductLike(Product product, User user) {
        Optional<ProductLike> existingLike =
                productLikeRepository.findByProductIdAndUserId(product.getId(), user.getId());

        if (existingLike.isPresent()) {
            productLikeRepository.delete(existingLike.get());
        } else {
            ProductLike newLike = ProductLike.createProductLike(product, user);
            productLikeRepository.save(newLike);
        }
    }

    public boolean isLiked(Long productId, Long userId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException();
        }
        return productLikeRepository.existsByProductIdAndUserId(productId, userId);
    }

    public Map<Long, Boolean> getLikeStatusMap(Long userId, List<Long> productIds) {
        if (userId == null || productIds.isEmpty()) {
            return productIds.stream()
                    .collect(Collectors.toMap(id -> id, id -> false));
        }

        List<Long> likedProductIds = productLikeRepository.findLikedProductIds(userId, productIds);

        return productIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        likedProductIds::contains
                ));
    }
}
