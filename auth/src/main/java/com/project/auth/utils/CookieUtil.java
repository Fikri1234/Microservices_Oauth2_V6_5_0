package com.project.auth.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by user on 14:58 05/07/2025, 2025
 */

@Component
@RequiredArgsConstructor
public class CookieUtil {

    @Value("${cookie.http-only:true}")
    private boolean httpOnly;
    @Value("${cookie.secure:false}")
    private boolean secure;

    @Value("${cookie.path:/}")
    private String path;

    public void addCookie(HttpServletResponse response, String name, String value, Instant expiresAt) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath(path);

        if (expiresAt != null) {
            int maxAge = (int) Duration.between(Instant.now(), expiresAt).getSeconds();
            cookie.setMaxAge(Math.max(maxAge, 0));
        } else {
            cookie.setMaxAge(300); // 5 minutes Session cookie
        }

        response.addCookie(cookie);
    }

    public void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(name))
                .findFirst();

        return cookie.map(Cookie::getValue).orElse(null);
    }

}
