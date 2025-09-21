package com.lokoko.global.utils;

public final class TikTokConstants {
    // BASE URL
    public static final String AUTHORIZE_BASE_URL = "https://www.tiktok.com/v2/auth/authorize/";
    public static final String TOKEN_PATH = "/v2/oauth/token/";
    public static final String USER_INFO_PATH = "/v2/user/info/";
    public static final String VIDEO_LIST_PATH = "/v2/video/list/";
    public static final String VIDEO_QUERY_PATH = "/v2/video/query/";

    // OAuth 인증관련 parameter
    public static final String PARAM_RESPONSE_TYPE = "?response_type=code";
    public static final String PARAM_CLIENT_KEY = "&client_key=";
    public static final String PARAM_REDIRECT_URI = "&redirect_uri=";
    public static final String PARAM_SCOPE = "&scope=";
    
    // 토큰 요청 parameter
    public static final String PARAM_GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_AUTH_CODE = "authorization_code";
    public static final String PARAM_CODE = "code";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String CLIENT_KEY = "client_key";
    public static final String PARAM_CLIENT_SECRET = "client_secret";
    
    // 틱톡에서, user_info 에 접근하기 위한 parameter
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    public static final String PARAM_FIELDS = "fields";
    public static final String DEFAULT_FIELDS = "open_id,union_id,follower_count,following_count,likes_count,video_count";
    
    // 비디오 관련 정보 가져오기 위한 parameter
    public static final String PARAM_MAX_COUNT = "max_count";
    public static final String PARAM_CURSOR = "cursor";
    public static final String VIDEO_FIELDS = "id,create_time,view_count,like_count,comment_count,share_count,share_url";
    private TikTokConstants() {
    }
}