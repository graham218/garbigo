package com.thepeacemakers.util;

public class Constants {
    
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60; // 24 hours
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 * 60; // 7 days
    
    public static final String DEFAULT_ROLE = "CLIENT";
    
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;
    
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/**",
            "/api/v1/public/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
    
    public static final String[] ADMIN_ENDPOINTS = {
            "/api/v1/admin/**"
    };
    
    public static final String CLOUDINARY_FOLDER = "garbigo/profiles";
}