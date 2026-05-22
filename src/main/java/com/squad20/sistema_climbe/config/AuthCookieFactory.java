package com.squad20.sistema_climbe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieFactory {

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String REFRESH_PATH = "/api/auth/refresh";

    @Value("${app.cookie.secure:false}")
    private boolean secure;

    @Value("${app.cookie.same-site:Lax}")
    private String sameSite;

    @Value("${app.cookie.access-token-max-age-seconds:1800}")
    private long accessTokenMaxAgeSeconds;

    @Value("${app.cookie.refresh-token-max-age-seconds:604800}")
    private long refreshTokenMaxAgeSeconds;

    public HttpHeaders createAuthCookies(String accessToken, String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie(accessToken, accessTokenMaxAgeSeconds).toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie(refreshToken, refreshTokenMaxAgeSeconds).toString());
        return headers;
    }

    public HttpHeaders clearAuthCookies() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie("", 0).toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie("", 0).toString());
        return headers;
    }

    public ResponseCookie accessTokenCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(ACCESS_TOKEN, token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite(sameSite)
                .build();
    }

    private ResponseCookie refreshTokenCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN, token)
                .httpOnly(true)
                .secure(secure)
                .path(REFRESH_PATH)
                .maxAge(maxAgeSeconds)
                .sameSite(sameSite)
                .build();
    }
}
