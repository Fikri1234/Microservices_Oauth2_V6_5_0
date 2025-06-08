package com.project.auth.security;

import com.project.auth.model.entity.OAuthClientDetails;
import com.project.auth.repository.OAuthClientDetailsRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 5:44 27/05/2025, 2025
 */
@Component
@RequiredArgsConstructor
public class DatabaseOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final OAuth2AuthorizationService authorizationService;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {

        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);

        if (authorization == null) {
            throw new OAuth2IntrospectionException("Token not found");
        }

        OAuth2Authorization.Token accessToken = authorization.getAccessToken();
        if (accessToken == null) {
            throw new OAuth2IntrospectionException("Access token not found in authorization.");
        }

        // IMPORTANT: Check for token expiry
        if (accessToken.isExpired()) {
            // Optionally, you might want to remove the expired token from the store here
            authorizationService.remove(authorization); // Consider background cleanup or on-demand removal
            throw new OAuth2IntrospectionException("Token is expired.");
        }

        Authentication principal = authorization.getAttribute(Principal.class.getName());
        if (principal == null) {
            throw new OAuth2IntrospectionException("Principal not found");
        }

        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
        if (principal instanceof AbstractAuthenticationToken) {
            authorities = ((AbstractAuthenticationToken) principal).getAuthorities();
        }

        Map<String, Object> attributes = new HashMap<>(authorization.getAttributes());
        attributes.put("client_id", authorization.getRegisteredClientId());
        attributes.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).toList());
//        attributes.put("token", accessToken);

        return new DefaultOAuth2AuthenticatedPrincipal(principal.getName(), attributes, (Collection<GrantedAuthority>) authorities);
    }
}


