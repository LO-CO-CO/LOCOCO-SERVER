package com.lokoko.domain.productReview.mapper;

import com.lokoko.domain.media.api.dto.repsonse.MediaPresignedUrlResponse;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.ProductOption;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import com.lokoko.domain.productReview.api.dto.request.ReviewRequest;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewListResponse;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.MainImageReview;
import com.lokoko.domain.productReview.api.dto.response.MainImageReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.MainVideoReview;
import com.lokoko.domain.productReview.api.dto.response.MainVideoReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.ReviewReceiptResponse;
import com.lokoko.domain.productReview.api.dto.response.ReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewListResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewResponse;
import com.lokoko.domain.productReview.domain.entity.Review;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.global.common.response.PageableResponse;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReviewMapper {

    /**
     * presigned URL 기반으로 영수증 응답 DTO 생성
     *
     * @param presignedUrl S3로 업로드할 영수증 이미지 URL 목록
     * @return ReviewReceiptResponse (응답 DTO)
     */
    default ReviewReceiptResponse toReviewReceiptUrl(List<String> presignedUrl) {
        return new ReviewReceiptResponse(presignedUrl);
    }

    /**
     * presigned URL 기반으로 이미지/동영상 응답 DTO 생성
     *
     * @param urls 미디어 파일 URL 목록 (S3 presigned URL)
     * @return ReviewMediaResponse (응답 DTO)
     */
    default MediaPresignedUrlResponse toReviewMediaResponse(List<String> urls) {
        return new MediaPresignedUrlResponse(urls);
    }

    /**
     * 메인 이미지 리뷰 리스트를 감싸는 응답 객체로 변환
     *
     * @param rankedList 순위가 매겨진 이미지 리뷰 리스트
     * @return MainImageReviewResponse
     */
    default MainImageReviewResponse toMainImageReviewResponse(List<MainImageReview> rankedList) {
        return new MainImageReviewResponse(rankedList);
    }

    /**
     * 메인 동영상 리뷰 리스트를 감싸는 응답 객체로 변환
     *
     * @param rankedList 순위가 매겨진 동영상 리뷰 리스트
     * @return MainVideoReviewResponse
     */
    default MainVideoReviewResponse toMainVideoReviewResponse(List<MainVideoReview> rankedList) {
        return new MainVideoReviewResponse(rankedList);
    }

    /**
     * Review 엔티티를 기반으로 리뷰 응답 DTO 변환
     *
     * @param review 리뷰 엔티티
     * @return ReviewResponse (리뷰 작성 결과 DTO)
     */
    @Mapping(target = "reviewId", source = "id")
    ReviewResponse toReviewResponse(Review review);


    /**
     * 리뷰 생성 요청 객체 + 유저 + 상품 + 옵션 정보를 Review 엔티티로 변환
     *
     * @param request 리뷰 작성 요청 DTO
     * @param user    리뷰 작성자 (유저)
     * @param product 리뷰 대상 상품
     * @param option  선택된 옵션 (nullable 가능)
     * @return Review 엔티티
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", source = "user")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "productOption", source = "option")
    @Mapping(target = "rating", expression = "java(com.lokoko.domain.productReview.domain.entity.enums.Rating.fromValue(request.rating()))")
    @Mapping(target = "positiveContent", source = "request.positiveComment")
    @Mapping(target = "negativeContent", source = "request.negativeComment")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "receiptUploaded", ignore = true)
    Review toReview(ReviewRequest request, User user, Product product, @Nullable ProductOption option);


    @Mapping(target = "rank", source = "ranking")
    MainImageReview withRanking(MainImageReview review, int ranking);

    @Mapping(target = "rank", source = "ranking")
    MainVideoReview withRanking(MainVideoReview review, int ranking);

    default List<MainImageReview> addRankingToImageReviews(List<MainImageReview> reviews) {
        return IntStream.range(0, reviews.size())
                .mapToObj(i -> withRanking(reviews.get(i), i + 1))
                .toList();
    }

    default List<MainVideoReview> addRankingToVideoReviews(List<MainVideoReview> reviews) {
        return IntStream.range(0, reviews.size())
                .mapToObj(i -> withRanking(reviews.get(i), i + 1))
                .toList();
    }


    /**
     * 중분류/소분류에 따른 리뷰 목록 & 페이징 응답 매핑
     *
     * @param reviews        사진 리뷰 리스트 DTO
     * @param middleCategory 중분류 카테고리
     * @param subCategory    소분류 카테고리 (없으면 null)
     * @param pageInfo       페이징 정보
     * @return 카테고리별 리뷰 페이지 응답 DTO
     */
    @Mapping(target = "searchQuery",
            expression = "java(subCategory == null ? middleCategory.getDisplayName() : subCategory.getDisplayName())")
    @Mapping(target = "parentCategoryName",
            expression = "java(subCategory == null ? middleCategory.getParent().getDisplayName() : subCategory.getMiddleCategory().getParent().getDisplayName())")
    @Mapping(target = "reviews", source = "reviews")
    @Mapping(target = "pageInfo", source = "pageInfo")
    ImageReviewListResponse toImageReviewListResponse(
            List<ImageReviewResponse> reviews,
            MiddleCategory middleCategory,
            @Nullable SubCategory subCategory,
            PageableResponse pageInfo
    );


    /**
     * 중분류/소분류에 따른 리뷰 목록 & 페이징 응답 매핑
     *
     * @param reviews        영상 리뷰 리스트 DTO
     * @param middleCategory 중분류 카테고리
     * @param subCategory    소분류 카테고리 (없으면 null)
     * @param pageInfo       페이징 정보
     * @return 카테고리별 리뷰 페이지 응답 DTO
     */
    @Mapping(target = "searchQuery",
            expression = "java(subCategory == null ? middleCategory.getDisplayName() : subCategory.getDisplayName())")
    @Mapping(target = "parentCategoryName",
            expression = "java(subCategory == null ? middleCategory.getParent().getDisplayName() : subCategory.getMiddleCategory().getParent().getDisplayName())")
    @Mapping(target = "reviews", source = "reviews")
    @Mapping(target = "pageInfo", source = "pageInfo")
    VideoReviewListResponse toVideoReviewListResponse(
            List<VideoReviewResponse> reviews,
            MiddleCategory middleCategory,
            @Nullable SubCategory subCategory,
            PageableResponse pageInfo
    );
}
