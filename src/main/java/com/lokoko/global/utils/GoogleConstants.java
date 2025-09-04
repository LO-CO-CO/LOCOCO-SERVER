package com.lokoko.global.utils;

public final class GoogleConstants {
    // Token API
    public static final String TOKEN_PATH = "/token";

    public static final String USERINFO_PATH = "/v1/userinfo";

    // OAuth Authorization
    public static final String AUTHORIZE_BASE_URL = "https://accounts.google.com/o/oauth2/auth";
    public static final String PARAM_RESPONSE_TYPE = "?response_type=code";
    public static final String PARAM_CLIENT_ID = "&client_id=";
    public static final String PARAM_REDIRECT_URI = "&redirect_uri=";
    public static final String PARAM_SCOPE = "&scope=";
    public static final String PARAM_STATE = "&state=";

    // Token request params
    public static final String PARAM_GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_AUTH_CODE = "authorization_code";
    public static final String PARAM_CODE = "code";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String CLIENT_ID = "client_id";
    public static final String PARAM_CLIENT_SECRET = "client_secret";

    // User Info API
    public static final String USERINFO_BASE_URL = "https://openidconnect.googleapis.com";

    private GoogleConstants() {
    }
}