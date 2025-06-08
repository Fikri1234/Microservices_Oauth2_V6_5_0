package com.project.auth.security;

import com.project.auth.model.entity.OAuthClientDetails;
import com.project.auth.repository.OAuthClientDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 3:24 27/05/2025, 2025
 */

//@Component
//@RequiredArgsConstructor
public class DynamicOpaqueTokenIntrospector /*implements OpaqueTokenIntrospector*/ {

//    private final OAuthClientDetailsRepository clientRepo;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public OAuth2AuthenticatedPrincipal introspect(String token) {
//        // Load all clients from DB (or use cache)
//        List<OAuthClientDetails> clients = clientRepo.findAll();
//
//        for (OAuthClientDetails client : clients) {
//            try {
//                OpaqueTokenIntrospector introspector = new NimbusOpaqueTokenIntrospector(
//                        "http://localhost:8081/oauth2/introspect",
//                        client.getClientId(),
//                        client.getClientSecret()
//                );
//                return introspector.introspect(token);
//            } catch (OAuth2IntrospectionException e) {
//                // Try next client
//            }
//        }
//
//        throw new OAuth2IntrospectionException("Token introspection failed for all clients.");
//    }

//    @Override
//    public OAuth2AuthenticatedPrincipal introspect(String token) {
//        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
//
//        if (authorization == null || authorization.getAccessToken().isExpired()) {
//            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", "Token not found or expired", null));
//        }
//
//        RegisteredClient client = authorization.getRegisteredClient();
//        // Optionally verify client credentials here if needed.
//        // This is typically done via introspection endpoint (if external), but can be skipped if token is self-issued.
//
//        OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
//        Map<String, Object> attributes = new HashMap<>(authorization.getAttributes());
//        attributes.put(OAuth2TokenType.ACCESS_TOKEN.getValue(), accessToken.getTokenValue());
//
//        return new DefaultOAuth2AuthenticatedPrincipal(
//                authorization.getPrincipalName(),
//                attributes,
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
//        );
//    }
}
