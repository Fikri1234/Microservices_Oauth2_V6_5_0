package com.project.auth.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * Created by user on 0:31 27/05/2025, 2025
 */

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final OAuth2AuthorizationService authorizationService;


    @GetMapping("/userinfo")
    public Map<String, Object> userInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();

        return Map.of(
                "sub", authentication.getName(),
                "attributes", principal.getAttributes(),
                "authorities", principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }

    @GetMapping("/session-info")
    public Map<String, Object> sessionInfo(HttpSession session) {
        return Map.of(
                "id", session.getId(),
                "creationTime", new Date(session.getCreationTime()),
                "lastAccessedTime", new Date(session.getLastAccessedTime()),
                "maxInactiveInterval (sec)", session.getMaxInactiveInterval()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);

        if (authorization != null) {
            authorizationService.remove(authorization);
        }

        return ResponseEntity.ok("Logged out successfully");
    }

}
