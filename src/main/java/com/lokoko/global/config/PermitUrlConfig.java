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
                "/api/products/details/{productId}/youtube",
                "/api/campaigns/upcoming",
                "/api/admin/login",
                "/api/reviews/brands/videos",
                "/api/reviews/brands/images",
                "/api/product-brand"
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
                "/api/campaigns/{campaignId}",
                "/api/campaigns",
                "/api/auth/sns/tiktok/connect",
                "/api/auth/sns/tiktok/callback",
                "/api/auth/sns/instagram/connect",
                "/api/auth/sns/instagram/callback"
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
     * 4) Customer 전용 - Customer 권한 필요
     */
    public String[] getCustomerUrl() {
        return new String[]{
                "/api/customer/profile/image",
                "/api/customer/profile",
                "/api/customer/sns-status",
        };
    }

    /**
     * 5) Creator 전용 - Creator 권한 필요
     */
    public String[] getCreatorUrl() {
        return new String[]{
                "/api/campaigns/media",
                "/api/creator/register/info",
                "/api/creator/register/sns-status",
                "/api/creator/register/complete",
                "/api/creator/profile",
                "/api/creator/profile/{campaignId}/address",
                "/api/customer/profile/image",
                "/api/creator/profile/address",
                "/api/creator-campaign/{campaignId}/participate",
                "/api/campaignReviews/{campaignId}/first",
                "/api/campaignReviews/{campaignId}/second",
                "/api/campaignReviews/my/participation",
                "/api/campaignReviews/my/participation/{campaignId}",
                "/api/creator/profile/campaigns"
        };
    }

    /**
     * 6) 브랜드 전용 (Brand) - Brand 권한 필요
     */
    public String[] getBrandUrl() {
        return new String[]{
                "/api/brands/my/campaigns/{campaignId}/creators/{creatorId}/review",
                "/api/brands/my/campaigns/{campaignId}/applicants",
                "/api/brands/my/campaigns/infos",
                "/api/brands/my/campaigns/in-review",
                "/api/brands/my/campaigns/{campaignId}/applicants/approve",
                "/api/brands/my/profile/stats",
                "/api/brands/my/campaigns",
                "/api/brands/my/reviews/{campaignReviewId}/revision-request",
                "/api/brands/my/campaigns/drafts/{campaignId}",
                "/api/brands/my/campaigns/{campaignId}/publish",
                "/api/brands/my/campaigns/{campaignId}/draft",
                "/api/brands/my/campaigns/drafts",
                "/api/brands/my/campaigns/publish",
                "/api/brands/register/info",
                "/api/brands/profile/image",
                "/api/brands/profile",
                "/api/brands/my/campaigns/creators/{campaignReviewId}/review"
        };
    }

    /**
     * 7) 관리자 전용 (Admin) - ROLE_ADMIN
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
