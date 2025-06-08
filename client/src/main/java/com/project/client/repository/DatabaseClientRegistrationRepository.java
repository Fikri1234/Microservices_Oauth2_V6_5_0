package com.project.client.repository;

import com.project.client.model.entity.OAuthClientDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by user on 16:56 21/05/2025, 2025
 */

@Component
@RequiredArgsConstructor
public class DatabaseClientRegistrationRepository implements ClientRegistrationRepository {

    private final OAuthClientDetailsRepository repository;
    private final Map<String, ClientRegistration> registrations = new ConcurrentHashMap<>();

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        System.out.println("registriaID: "+registrationId);
        return registrations.computeIfAbsent(registrationId, id ->
                repository.findById(id).map(this::toClientRegistration)
                .orElse(null));
    }

    private ClientRegistration toClientRegistration(OAuthClientDetails entity) {
        Function<String, String[]> splitAndTrim = s -> {
            if (s == null || s.trim().isEmpty()) {
                return new String[0];
            }
            return Arrays.stream(s.split(","))
                    .map(String::trim)
                    .filter(str -> !str.isEmpty())
                    .toArray(String[]::new);
        };

        String[] grantTypes = splitAndTrim.apply(entity.getAuthorizedGrantTypes());
        String[] authMethods = splitAndTrim.apply(entity.getClientAuthenticationMethods());
        String[] scopes = splitAndTrim.apply(entity.getScopes());

        String[] redirectUris = splitAndTrim.apply(entity.getWebServerRedirectUris());
        String redirectUri = redirectUris.length > 0 ? redirectUris[0] : ""; // fallback if empty

        // Use first auth method and grant type if available
        ClientAuthenticationMethod authMethod = authMethods.length > 0
                ? new ClientAuthenticationMethod(authMethods[0])
                : new ClientAuthenticationMethod("client_secret_basic"); // default fallback

        AuthorizationGrantType grantType = grantTypes.length > 0
                ? new AuthorizationGrantType(grantTypes[0])
                : new AuthorizationGrantType("authorization_code"); // default fallback

//        return ClientRegistration.withRegistrationId(entity.getId())
        return ClientRegistration.withRegistrationId(entity.getClientId())
                .clientId(entity.getClientId())
                .clientName(entity.getClientId())
                .clientSecret(entity.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(grantType)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
//                .redirectUri("{baseUrl}/login/oauth2/code/" + entity.getClientId())
                .scope(scopes)
                .authorizationUri("http://localhost:8081/oauth2/authorize")
                .tokenUri("http://localhost:8081/oauth2/token")
                .userInfoUri("http://localhost:8081/userinfo") // Optional if OpenID Connect
                .userInfoAuthenticationMethod(AuthenticationMethod.HEADER)
                .userNameAttributeName(IdTokenClaimNames.SUB) // required if OpenID Connect
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
        return Arrays.stream(splitAndTrim(grantTypes))
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
