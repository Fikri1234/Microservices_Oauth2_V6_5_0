package com.project.auth.security;

import com.project.auth.model.entity.User;
import com.project.auth.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 13:34 03/07/2025, 2025
 */


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2TokenIssuer {

    private final OAuth2AuthorizationService authService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final CookieUtil cookieUtil;

    public TokenResponse issueToken(HttpServletResponse response, Authentication principal, User user, RegisteredClient client) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Authentication grantAuthentication;
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            grantAuthentication = new OAuth2AuthenticationToken(oauthToken.getPrincipal(), principal.getAuthorities(), client.getClientId());
        } else {
            grantAuthentication = principal;
        }

        OAuth2AccessToken accessToken = null;
        OAuth2RefreshToken refreshToken = null;

        // 2. Create OAuth2TokenContext
        OAuth2TokenContext accessContext = DefaultOAuth2TokenContext.builder()
                .registeredClient(client)
                .principal(principal)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // or CLIENT_CREDENTIALS, etc.
                .authorizationGrant(grantAuthentication)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizedScopes(client.getScopes())
                .build();

        OAuth2TokenContext refreshContext = DefaultOAuth2TokenContext.builder()
                .registeredClient(client)
                .principal(principal)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrant(grantAuthentication)
                .tokenType(OAuth2TokenType.REFRESH_TOKEN)
                .authorizedScopes(client.getScopes())
                .build();

        // 3. Generate tokens
        OAuth2Token generatedAccessToken = tokenGenerator.generate(accessContext);
        log.info("Class: {}", generatedAccessToken.getClass());
        if (generatedAccessToken != null && generatedAccessToken instanceof OAuth2AccessToken) {
            accessToken = new OAuth2AccessToken(
                    ((OAuth2AccessToken) generatedAccessToken).getTokenType(),
                    ((OAuth2AccessToken) generatedAccessToken).getTokenValue(),
                    ((OAuth2AccessToken) generatedAccessToken).getIssuedAt(),
                    ((OAuth2AccessToken) generatedAccessToken).getExpiresAt(),
                    ((OAuth2AccessToken) generatedAccessToken).getScopes()
            );
        } else {
            throw new RuntimeException("Failed to generate access token");
        }

        OAuth2Token generateRefreshToken = tokenGenerator.generate(refreshContext);
        if (generateRefreshToken != null && generateRefreshToken instanceof OAuth2RefreshToken rt) {
            refreshToken = rt;
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", principal.getName()); // or user.getId() etc.
        claims.put("scope", client.getScopes());

        // 4. Build and save the authorization
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(client)
                .principalName(principal.getName())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizedScopes(client.getScopes())
                .attribute(Principal.class.getName(), principal)
//                .accessToken(accessToken)
                .token(accessToken, metadata -> {
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, claims); // important for indexing!
                })
                ;

        if (refreshToken != null) {
            authorizationBuilder
//                    .refreshToken(refreshToken)
                    .token(refreshToken, metadata -> {
                        metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, claims);
                    })
            ;
        }

        OAuth2Authorization authorization = authorizationBuilder.build();
        log.info("Authorization access token: {}", authorization.getAccessToken());
        log.info("Authorization refresh token: {}", authorization.getRefreshToken() );
        authService.save(authorization);

        String refreshTokenValue = refreshToken != null ? refreshToken.getTokenValue() : null;
        Instant refreshTokenExpiresAt = refreshToken != null ? refreshToken.getExpiresAt() : null;

        // Access Token Cookie
        cookieUtil.addCookie(response, "access_token", accessToken.getTokenValue(), accessToken.getExpiresAt());

        // Refresh Token Cookie
        if (refreshToken != null) {
            cookieUtil.addCookie(response, "refresh_token", refreshTokenValue, refreshTokenExpiresAt);
        }

        return new TokenResponse(accessToken.getTokenValue(), accessToken.getExpiresAt(), refreshTokenValue, refreshTokenExpiresAt);
    }
}
