package com.project.client.configuration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by user on 15:51 29/05/2025, 2025
 */
@Configuration
public class SessionLoggingFilterConfig extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("Session ID: " + session.getId());
        } else {
            System.out.println("No session found");
        }

        String cookies = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .map(c -> c.getName() + "=" + c.getValue())
                .collect(Collectors.joining("; "));
        System.out.println("Cookies: " + cookies);

        filterChain.doFilter(request, response);
    }
}