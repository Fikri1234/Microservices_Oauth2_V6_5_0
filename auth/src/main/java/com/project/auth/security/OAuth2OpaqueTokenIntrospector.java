package com.project.auth.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.Collections;

/**
 * Created by user on 2:09 27/05/2025, 2025
 */

//@AllArgsConstructor
public class OAuth2OpaqueTokenIntrospector /*implements OpaqueTokenIntrospector*/ {

//    private final OAuth2AuthorizationService authorizationService;
//    private final RegisteredClientRepository registeredClientRepository;
//
//    @Override
//    public OAuth2AuthenticatedPrincipal introspect(String token) {
//        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
//        if (authorization == null || !authorization.getAccessToken().isActive()) {
//            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token"));
//        }
//
//        RegisteredClient client = registeredClientRepository.findById(authorization.getId());
//        if (client == null) {
//            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token"));
//        }
//
//        return authorization.getPrincipalName() != null
//                ? new DefaultOAuth2AuthenticatedPrincipal(
//                authorization.getPrincipalName(),
//                authorization.getAttributes(),
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
//                : null;
//    }
}
