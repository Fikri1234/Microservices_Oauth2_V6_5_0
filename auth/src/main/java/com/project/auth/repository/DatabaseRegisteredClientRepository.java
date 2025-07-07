package com.project.auth.repository;

import com.project.auth.model.entity.OAuthClientDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by user on 6:42 19/05/2025, 2025
 */

@RequiredArgsConstructor
public class DatabaseRegisteredClientRepository implements RegisteredClientRepository {

    private final OAuthClientDetailsRepository clientDetailsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void save(RegisteredClient registeredClient) {
        // implement if needed
        throw new UnsupportedOperationException("Dynamic update not supported here");
    }

    @Override
    public RegisteredClient findById(String id) {
        System.out.println("Searec RegisteredClient with id: " + id);
        return clientDetailsRepository.findById(id).map(this::toRegisteredClient).orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        RegisteredClient registeredClient= clientDetailsRepository.findByClientId(clientId).map(this::toRegisteredClient).orElse(null);
        System.out.println("Saving RegisteredClient with id: " + registeredClient.getId());
        System.out.println("Saving RegisteredClient with getClientId: " + registeredClient.getClientId());
        return registeredClient;
    }

    private RegisteredClient toRegisteredClient(OAuthClientDetails clientDetails) {
        String clientSecret = passwordEncoder.encode(clientDetails.getClientSecret());
        System.out.println("Converting OAuthClientDetails secret: " + clientDetails.getClientSecret());
        System.out.println("Encoded secret: " + clientSecret);
        return RegisteredClient.withId(clientDetails.getClientId())
                .clientId(clientDetails.getClientId())
                .clientSecret(clientSecret)
                .authorizationGrantTypes((grants) ->
                        grants.addAll(parseGrantTypes(clientDetails.getAuthorizedGrantTypes())))
                .redirectUris(uris ->
                        Arrays.stream(clientDetails.getWebServerRedirectUris().split(","))
                                .forEach(uris::add))
                .scopes(scopes ->
                        Arrays.stream(clientDetails.getScopes().split(","))
                                .forEach(scopes::add))
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofSeconds(clientDetails.getAccessTokenValidity() != null ? clientDetails.getAccessTokenValidity() : 1800))
                        .refreshTokenTimeToLive(Duration.ofSeconds(clientDetails.getRefreshTokenValidity() != null ? clientDetails.getRefreshTokenValidity() : 3600))
                        .accessTokenFormat(OAuth2TokenFormat.REFERENCE) // opaque token
                        .build())
                .clientAuthenticationMethods(methods -> methods.addAll(parseClientAuthenticationMethods(clientDetails.getClientAuthenticationMethods())))
                .build();
    }

    private String[] splitAndTrim(String s) {
        if (s == null || s.trim().isEmpty()) {
            return new String[0];
        }
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .toArray(String[]::new);
    }
    private Set<ClientAuthenticationMethod> parseClientAuthenticationMethods(String methodsString) {
        return Arrays.stream(splitAndTrim(methodsString))
                .map(this::mapToClientAuthenticationMethod)
                .collect(Collectors.toSet());
    }
    private ClientAuthenticationMethod mapToClientAuthenticationMethod(String method) {
        return switch (method.toLowerCase()) {
            case "client_secret_basic" -> ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
            case "client_secret_post" -> ClientAuthenticationMethod.CLIENT_SECRET_POST;
            case "none" -> ClientAuthenticationMethod.NONE;
            case "client_secret_jwt" -> ClientAuthenticationMethod.CLIENT_SECRET_JWT;
            case "private_key_jwt" -> ClientAuthenticationMethod.PRIVATE_KEY_JWT;
            case "tls_client_auth" -> ClientAuthenticationMethod.TLS_CLIENT_AUTH;
            case "self_signed_tls_client_auth" -> ClientAuthenticationMethod.SELF_SIGNED_TLS_CLIENT_AUTH;
            default -> throw new IllegalArgumentException("Unsupported client authentication method: " + method);
        };
    }

    private Set<AuthorizationGrantType> parseGrantTypes(String grantTypes) {
        return Arrays.stream(grantTypes.split(","))
                .map(String::trim)
                .map(this::toAuthorizationGrantType)
                .collect(Collectors.toSet());
    }

    private AuthorizationGrantType toAuthorizationGrantType(String type) {
        return switch (type.toLowerCase()) {
            case "authorization_code" -> AuthorizationGrantType.AUTHORIZATION_CODE;
            case "client_credentials" -> AuthorizationGrantType.CLIENT_CREDENTIALS;
            case "refresh_token" -> AuthorizationGrantType.REFRESH_TOKEN;
//            case "password" -> AuthorizationGrantType.PASSWORD; // Deprecated but still usable
            case "device_code" -> AuthorizationGrantType.DEVICE_CODE;
            case "jwt_bearer" -> AuthorizationGrantType.JWT_BEARER;
            case "urn:ietf:params:oauth:grant-type:token-exchange" -> AuthorizationGrantType.TOKEN_EXCHANGE;
            default -> throw new IllegalArgumentException("Unsupported grant type: " + type);
        };
    }
}
