package com.lokoko.global.auth.provider.insta.config;

public class InstagramConstants {
    private InstagramConstants() {
    }

    // ========== Facebook OAuth (Instagram Graph API 사용을 위한 Facebook 로그인) ==========
    public static final String FACEBOOK_AUTHORIZE_URL = "https://www.facebook.com/v23.0/dialog/oauth";
    public static final String FACEBOOK_TOKEN_URL = "https://graph.facebook.com/v23.0/oauth/access_token";
    public static final String FACEBOOK_GRAPH_API_BASE = "https://graph.facebook.com/v23.0";

    // ========== Instagram Basic Display API (Deprecated - 기존 방식) ==========
    public static final String AUTHORIZE_BASE_URL = "https://www.instagram.com/oauth/authorize";
    public static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    public static final String LONG_LIVED_TOKEN_URL = "https://graph.instagram.com/access_token";
    public static final String REFRESH_TOKEN_URL = "https://graph.instagram.com/refresh_access_token";

    // ========== Query/Form Parameters ==========
    public static final String PARAM_CLIENT_ID = "client_id";
    public static final String PARAM_CLIENT_SECRET = "client_secret";
    public static final String PARAM_REDIRECT_URI = "redirect_uri";
    public static final String PARAM_RESPONSE_TYPE = "response_type";
    public static final String PARAM_SCOPE = "scope";
    public static final String PARAM_STATE = "state";
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    public static final String PARAM_CODE = "code";
    public static final String PARAM_GRANT_TYPE = "grant_type";
    public static final String PARAM_FIELDS = "fields";
    public static final String RESPONSE_TYPE_CODE = "code";

    // ========== Grant Types ==========
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    public static final String GRANT_TYPE_IG_EXCHANGE_TOKEN = "ig_exchange_token";
    public static final String GRANT_TYPE_IG_REFRESH_TOKEN = "ig_refresh_token";

    // ========== Instagram Graph API Scopes ==========
    // Facebook 페이지 관리 권한
    public static final String SCOPE_PAGES_SHOW_LIST = "pages_show_list";
    public static final String SCOPE_PAGES_READ_ENGAGEMENT = "pages_read_engagement";
    public static final String SCOPE_PAGES_MANAGE_METADATA = "pages_manage_metadata";

    // Instagram 비즈니스 계정 권한
    public static final String SCOPE_INSTAGRAM_BASIC = "instagram_basic";
    public static final String SCOPE_INSTAGRAM_MANAGE_INSIGHTS = "instagram_manage_insights";
    public static final String SCOPE_INSTAGRAM_CONTENT_PUBLISH = "instagram_content_publish";

    // 공통 권한
    public static final String SCOPE_BUSINESS_MANAGEMENT = "business_management";
}
