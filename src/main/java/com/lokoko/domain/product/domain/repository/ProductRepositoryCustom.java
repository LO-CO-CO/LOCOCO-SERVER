package com.lokoko.domain.product.domain.repository;

import com.lokoko.domain.product.api.dto.response.NewProductProjection;
import com.lokoko.domain.product.api.dto.response.PopularProductProjection;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepositoryCustom {
    Slice<Product> searchByTokens(List<String> tokens, Pageable pageable);

    Slice<Product> findProductsByPopularityAndRating(MiddleCategory category, Pageable pageable);

    Slice<Product> findProductsByPopularityAndRating(MiddleCategory category, SubCategory subCategory,
                                                     Pageable pageable);

    Slice<PopularProductProjection> findPopularProductsWithDetails(MiddleCategory middleCategory, Long userId,
                                                                   Pageable pageable);

    Slice<NewProductProjection> findNewProductsWithDetails(
            MiddleCategory category,
            Long userId,
            Pageable pageable
    );
}
