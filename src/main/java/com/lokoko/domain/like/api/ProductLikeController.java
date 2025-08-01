package com.lokoko.domain.like.api;

import static com.lokoko.domain.like.api.message.ResponseMessage.PRODUCT_LIKE_TOGGLE_SUCCESS;

import com.lokoko.domain.like.api.dto.response.ToggleLikeResponse;
import com.lokoko.domain.like.application.service.ProductLikeService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PRODUCT LIKE")
@RestController
@RequestMapping("/api/likes/products/{productId}")
@RequiredArgsConstructor
public class ProductLikeController {
    private final ProductLikeService productLikeService;

    @Operation(summary = "상품 좋아요 토글 (추가/취소)")
    @PostMapping
    public ApiResponse<ToggleLikeResponse> toggleLike(@PathVariable final Long productId,
                                                      @Parameter(hidden = true) @CurrentUser Long userId) {
        productLikeService.toggleProductLike(productId, userId);
        boolean isLiked = productLikeService.isLiked(productId, userId);
        return ApiResponse.success(HttpStatus.OK, PRODUCT_LIKE_TOGGLE_SUCCESS.getMessage(),
                new ToggleLikeResponse(isLiked));
    }
}
