package com.lokoko.global.auth.provider.insta.config;

public class InstagramConstants {
    private InstagramConstants() {
    }

    public static final String AUTHORIZE_BASE_URL = "https://www.instagram.com/oauth/authorize";
    public static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    public static final String LONG_LIVED_TOKEN_URL = "https://graph.instagram.com/access_token";
    public static final String REFRESH_TOKEN_URL = "https://graph.instagram.com/refresh_access_token";

    // query/form keys
    public static final String PARAM_CLIENT_ID = "client_id";
    public static final String PARAM_CLIENT_SECRET = "client_secret";
    public static final String PARAM_REDIRECT_URI = "redirect_uri";
    public static final String PARAM_RESPONSE_TYPE = "response_type";
    public static final String PARAM_SCOPE = "scope";
    public static final String PARAM_STATE = "state";
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    public static final String PARAM_CODE = "code";
    public static final String PARAM_GRANT_TYPE = "grant_type";
    public static final String RESPONSE_TYPE_CODE = "code";

    // long-lived
    public static final String GRANT_TYPE_IG_EXCHANGE_TOKEN = "ig_exchange_token";

    // refresh
    public static final String GRANT_TYPE_IG_REFRESH_TOKEN = "ig_refresh_token";
}
