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

        if (entity.getClientId() == null || entity.getClientId().isEmpty()) {
            throw new IllegalArgumentException("Client ID must not be null or empty");
        }

        String[] grantTypes = splitAndTrim(entity.getAuthorizedGrantTypes());
        String[] authMethods = splitAndTrim(entity.getClientAuthenticationMethods());
        String[] scopes = splitAndTrim(entity.getScopes());

        String[] redirectUris = splitAndTrim(entity.getWebServerRedirectUris());
        String redirectUri = redirectUris.length > 0 ? redirectUris[0] : "{baseUrl}/login/oauth2/code/{registrationId}"; // fallback if empty

        // Use first auth method and grant type if available
        ClientAuthenticationMethod authMethod = authMethods.length > 0
                ? new ClientAuthenticationMethod(authMethods[0])
                : new ClientAuthenticationMethod("client_secret_basic"); // default fallback

        AuthorizationGrantType grantType = grantTypes.length > 0
                ? new AuthorizationGrantType(grantTypes[0])
                : new AuthorizationGrantType("authorization_code"); // default fallback

        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(entity.getClientId())
                .clientId(entity.getClientId())
                .clientName(entity.getResourceIds())
                .clientSecret(entity.getClientSecret())
//                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(authMethod)
                .authorizationGrantType(grantType)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
//                .redirectUri("{baseUrl}/login/oauth2/code/"  entity.getClientId())
                .scope(scopes)
                .authorizationUri("http://localhost:8081/oauth2/authorize")
                .tokenUri("http://localhost:8081/oauth2/token")
                .userInfoUri("http://localhost:8081/auth/userinfo") // Optional if OpenID Connect
                .userInfoAuthenticationMethod(AuthenticationMethod.HEADER)
                .userNameAttributeName(IdTokenClaimNames.SUB) // required if OpenID Connect
                ;

        if (entity.getClientId().toLowerCase().contains("google")) {
            builder
                    .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                    .tokenUri("https://oauth2.googleapis.com/token")
                    .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                    .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs");
        } else if (entity.getClientId().toLowerCase().contains("facebook")) {
            builder
                    .authorizationUri("https://www.facebook.com/v10.0/dialog/oauth")
                    .tokenUri("https://graph.facebook.com/v10.0/oauth/access_token")
                    .userInfoUri("https://graph.facebook.com/me?fields=id,name,email")
                    .jwkSetUri(null); // Facebook doesn't use JWKs
        }

        return builder.build();
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
