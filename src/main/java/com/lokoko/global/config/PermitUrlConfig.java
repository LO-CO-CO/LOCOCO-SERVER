package com.lokoko.global.config;

import org.springframework.stereotype.Component;

@Component
public class PermitUrlConfig {

    /**
     * 1) 전체 공개 (Public) - 토큰, @CurrentUser 필요 없음
     */
    public String[] getPublicUrl() {
        return new String[]{
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/health-check",
                "/api/auth/**",
                "/api/youtube/trends",
                "/api/reviews/image",
                "/api/reviews/video",
                "/api/reviews/details/video",
                "/api/reviews/{productId}/{userId}",
                "/api/products/details/{productId}/youtube"
        };
    }

    /**
     * 2) 선택 인증 (Optional Auth) - 토큰 있으면 읽어서 @CurrentUser 주입, 없어도 접근은 가능
     */
    public String[] getOptionalUrl() {
        return new String[]{
                "/api/reviews/details/image",
                "/api/reviews/details/{reviewId}/image",
                "/api/reviews/details/{reviewId}/video",
                "/api/products/categories/search",
                "/api/products/search",
                "/api/products/categories/new",
                "/api/products/categories/popular",
                "/api/products/details/{productId}",
        };
    }

    /**
     * 3) 인증 필요 (User) - 회원가입한 유저만 접근 가능 (둘다 필요)
     */
    public String[] getUserUrl() {
        return new String[]{
                "/api/likes/**",
                "/api/reviews/{productId}",
                "/api/reviews/media",
                "/api/reviews/receipt",
                "/api/reviews/{reviewId}"
        };
    }

    /**
     * 4) Creator 전용 - Creator 권한 필요
     */
    public String[] getCreatorUrl() {
        return new String[]{
                "/api/auth/sns/tiktok/connect",
                "/api/auth/sns/tiktok/callback",
        };
    }

    /**
     * 5) 관리자 전용 (Admin) - ROLE_ADMIN
     */
    public String[] getAdminUrl() {
        return new String[]{
                "/api/admin/**",
                "/api/products/crawl",
                "/api/products/crawl/new",
                "/api/products/crawl/options",
                "/api/products/search-fields/migrate",
                "/api/youtube/{productId}/crawl",
                "/api/youtube/trends/crawl",
                "/api/migration/**"
        };
    }

}
