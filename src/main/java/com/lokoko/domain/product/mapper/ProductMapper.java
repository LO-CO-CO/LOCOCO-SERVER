package com.lokoko.domain.product.mapper;

import com.lokoko.domain.product.api.dto.NewProductProjection;
import com.lokoko.domain.product.api.dto.PopularProductProjection;
import com.lokoko.domain.product.api.dto.response.CachedPopularProduct;
import com.lokoko.domain.product.api.dto.response.CachedPopularProductsResponse;
import com.lokoko.domain.product.api.dto.response.NewProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.PopularProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.ProductBasicResponse;
import com.lokoko.domain.product.api.dto.response.ProductDetailResponse;
import com.lokoko.domain.product.api.dto.response.ProductListItemResponse;
import com.lokoko.domain.product.api.dto.response.ProductOptionResponse;
import com.lokoko.domain.product.api.dto.response.ProductStatsResponse;
import com.lokoko.domain.product.api.dto.response.ProductYoutubeResponse;
import com.lokoko.domain.product.api.dto.response.ProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.RatingPercentResponse;
import com.lokoko.domain.product.api.dto.response.SearchProductsResponse;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.ProductOption;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import com.lokoko.global.common.response.PageableResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {


    /**
     * 중분류/소분류에 따른 상품 목록 & 페이징 응답 매핑
     *
     * @param products       상품 목록 아이템 DTO
     * @param middleCategory 중분류 카테고리
     * @param subCategory    소분류 카테고리 (없으면 null)
     * @param pageInfo       페이징 정보
     * @return 카테고리별 상품 페이지 응답 DTO
     */
    @Mapping(target = "searchQuery",
            expression = "java(subCategory == null ? middleCategory.getDisplayName() : subCategory.getDisplayName())")
    @Mapping(target = "parentCategoryName",
            expression = "java(subCategory == null ? middleCategory.getParent().getDisplayName() : subCategory.getMiddleCategory().getParent().getDisplayName())")
    ProductsByCategoryResponse toCategoryProductPageResponse(
            List<ProductListItemResponse> products,
            MiddleCategory middleCategory,
            SubCategory subCategory,
            PageableResponse pageInfo
    );

    /**
     * 메인 페이지 신상품 목록 응답 매핑
     *
     * @param products       신상품 DTO 리스트
     * @param middleCategory 중분류 카테고리
     * @param pageInfo       페이징 정보
     * @return 신상품 목록 응답 DTO
     */
    @Mapping(target = "searchQuery", source = "middleCategory.displayName")
    @Mapping(target = "products", expression = "java(products.stream().map(NewProductProjection::toProductResponse).toList())")
    NewProductsByCategoryResponse toCategoryNewProductResponse(
            List<NewProductProjection> products,
            MiddleCategory middleCategory,
            PageableResponse pageInfo
    );

    /**
     * 메인 페이지 인기상품 목록 응답 매핑
     *
     * @param products       인기상품 DTO 리스트
     * @param middleCategory 중분류 카테고리
     * @param pageInfo       페이징 정보
     * @return 인기상품 목록 응답 DTO
     */
    @Mapping(target = "searchQuery", source = "middleCategory.displayName")
    @Mapping(target = "products", expression = "java(products.stream().map(PopularProductProjection::toProductResponse).toList())")
    PopularProductsByCategoryResponse toCategoryPopularProductResponse(
            List<PopularProductProjection> products,
            MiddleCategory middleCategory,
            PageableResponse pageInfo
    );

    /**
     * 브랜드 + 상품명으로 검색한 결과 응답 매핑
     *
     * @param products 검색 결과 DTO 리스트
     * @param keyword  검색어
     * @param pageInfo 페이징 정보
     * @return 이름·브랜드 검색 응답 DTO
     */
    @Mapping(target = "searchQuery", source = "keyword")
    SearchProductsResponse toNameBrandProductResponse(
            List<ProductListItemResponse> products,
            String keyword,
            PageableResponse pageInfo
    );

    /**
     * 상품 상세조회(옵션·별점 포함) 응답 매핑
     *
     * @param response    기본 상품 정보 DTO
     * @param options     옵션 DTO 리스트
     * @param product     엔티티(추가 상세 정보)
     * @param starPercent 별점 비율 리스트
     * @param isLiked     사용자가 좋아요했는지 여부
     * @return 상품 상세조회 응답 DTO
     */
    @Mapping(target = "productId", source = "response.productId")
    @Mapping(target = "imageUrls", source = "response.imageUrls")
    @Mapping(target = "productOptions", source = "options")
    @Mapping(target = "productName", source = "response.productName")
    @Mapping(target = "brandName", source = "response.brandName")
    @Mapping(target = "unit", source = "response.unit")
    @Mapping(target = "reviewCount", source = "response.reviewCount")
    @Mapping(target = "rating", source = "response.rating")
    @Mapping(target = "starPercent", source = "starPercent")
    @Mapping(target = "isLiked", source = "isLiked")
    @Mapping(target = "normalPrice", source = "product.normalPrice")
    @Mapping(target = "productDetail", source = "product.productDetail")
    @Mapping(target = "ingredients", source = "product.ingredients")
    @Mapping(target = "oliveYoungUrl", source = "product.oliveYoungUrl")
    @Mapping(target = "q10Url", source = "product.qoo10Url")
    @Mapping(target = "middleCategory", source = "product.middleCategory")
    @Mapping(target = "subCategory", source = "product.subCategory")
    ProductDetailResponse toProductDetailResponse(
            ProductBasicResponse response,
            List<ProductOptionResponse> options,
            Product product,
            List<RatingPercentResponse> starPercent,
            Boolean isLiked
    );

    /**
     * 상품 상세조회 시 유튜브 URL 리스트만 반환하는 응답 매핑
     *
     * @param youtubeUrls 유튜브 URL 리스트
     * @return 유튜브 URL 응답 DTO
     */
    default ProductYoutubeResponse toProductDetailYoutubeResponse(List<String> youtubeUrls) {
        return new ProductYoutubeResponse(youtubeUrls);
    }

    // 내부 재사용 DTO

    /**
     * 상품 엔티티·통계·좋아요 여부를 ProductBasicResponse DTO로 변환
     *
     * @param products   상품 엔티티 목록
     * @param summaryMap 제품별 통계 정보
     * @param likedIds   사용자가 좋아요한 상품 ID 집합
     * @return DTO 리스트
     */
    default List<ProductBasicResponse> toProductBasicResponses(
            List<Product> products,
            Map<Long, ProductStatsResponse> summaryMap,
            Set<Long> likedIds
    ) {
        return products.stream()
                .map(product -> {
                    ProductStatsResponse summary = summaryMap.getOrDefault(
                            product.getId(),
                            new ProductStatsResponse("", 0L, 0.0)
                    );
                    boolean isLiked = likedIds.contains(product.getId());
                    return ProductBasicResponse.of(product, summary, isLiked);
                })
                .toList();
    }

    /**
     * 상품 엔티티·통계·좋아요 여부를 ProductListItemResponse DTO로 변환
     *
     * @param products   상품 엔티티 목록
     * @param summaryMap 제품별 통계 정보
     * @param likedIds   사용자가 좋아요한 상품 ID 집합
     * @return DTO 리스트
     */
    default List<ProductListItemResponse> toProductListItemResponses(
            List<Product> products,
            Map<Long, ProductStatsResponse> summaryMap,
            Set<Long> likedIds
    ) {
        return products.stream()
                .map(product -> toProductMainImageResponse(product,
                        summaryMap.getOrDefault(product.getId(), new ProductStatsResponse("", 0L, 0.0)),
                        likedIds.contains(product.getId())))
                .toList();
    }

    /**
     * 상품 목록용 아이템 DTO로 매핑
     *
     * @param product 엔티티
     * @param summary 대표 이미지·리뷰·평점 요약
     * @param isLiked 좋아요 여부
     * @return 목록 아이템 응답 DTO
     */
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "url", source = "summary.imageUrl")
    @Mapping(target = "productName", source = "product.productName")
    @Mapping(target = "brandName", source = "product.brandName")
    @Mapping(target = "unit", source = "product.unit")
    @Mapping(target = "reviewCount", source = "summary.reviewCount")
    @Mapping(target = "rating", source = "summary.avgRating")
    @Mapping(target = "isLiked", source = "isLiked")
    ProductListItemResponse toProductMainImageResponse(
            Product product,
            ProductStatsResponse summary,
            boolean isLiked
    );

    /**
     * 상품 옵션 DTO 매핑
     *
     * @param option 옵션 엔티티
     * @return 옵션 응답 DTO
     */
    ProductOptionResponse toProductOptionResponse(ProductOption option);

    /**
     * 프로젝션(NewProductProjection) → 기본 상품 DTO 매핑
     */
    ProductBasicResponse toProductResponse(NewProductProjection projection);

    /**
     * 프로젝션(PopularProductProjection) → 기본 상품 DTO 매핑
     */
    ProductBasicResponse toProductResponse(PopularProductProjection projection);


    default CachedPopularProductsResponse toCachedPopularProductResponse(
            List<PopularProductProjection> projections,
            MiddleCategory middleCategory,
            PageableResponse pageInfo) {

        List<CachedPopularProduct> products = projections.stream()
                .map(this::toCachedPopularProduct)
                .toList();

        return CachedPopularProductsResponse.builder()
                .searchQuery(middleCategory.getDisplayName())
                .products(products)
                .pageInfo(pageInfo)
                .build();
    }

    default CachedPopularProduct toCachedPopularProduct(PopularProductProjection projection) {
        List<String> images = Optional.ofNullable(projection.imageUrl())
                .filter(u -> !u.isBlank())
                .map(u -> u.contains(",")
                        ? Arrays.stream(u.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList()
                        : List.of(u))
                .orElseGet(List::of);

        return CachedPopularProduct.builder()
                .productId(projection.productId())
                .imageUrls(images)
                .productName(projection.productName())
                .brandName(projection.brandName())
                .unit(projection.unit())
                .reviewCount(projection.reviewCount())
                .rating(projection.avgRating())
                .build();
    }
}
