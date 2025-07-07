package com.project.auth.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by user on 6:05 19/05/2025, 2025
 */

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Table(name="OAUTH_CLIENT_DETAILS"/*, schema = "core_db"*/)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OAuthClientDetails {

//    @Id
//    @Column(name = "ID")
//    String id = UUID.randomUUID().toString();

    @Id
    @Column(name = "CLIENT_ID")
    String clientId;

    @Column(name = "RESOURCE_IDS")
    String resourceIds;

    @Column(name = "CLIENT_SECRET")
    String clientSecret;

    @Column(name = "SCOPES")
    String scopes;

    @Column(name = "CLIENT_AUTHENTICATION_METHODS")
    String clientAuthenticationMethods;

    @Column(name = "AUTHORIZED_GRANT_TYPES")
    String authorizedGrantTypes;

    @Column(name = "WEB_SERVER_REDIRECT_URIS")
    String webServerRedirectUris;

    @Column(name = "AUTHORITIES")
    String authorities;

    @Column(name = "ACCESS_TOKEN_VALIDITY")
    Integer accessTokenValidity;

    @Column(name = "REFRESH_TOKEN_VALIDITY")
    Integer refreshTokenValidity;

    @Column(name = "ADDITIONAL_INFORMATION")
    String additionalInformation;

    @Column(name = "AUTOAPPROVE")
    String autoApprove;

    public ClientRegistration toClientRegistration() {

        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("Client ID must not be null or empty");
        }

        String[] grantTypes = splitAndTrim(authorizedGrantTypes);
        String[] authMethods = splitAndTrim(clientAuthenticationMethods);
        String[] scopesArr = splitAndTrim(scopes);

        String[] redirectUris = splitAndTrim(webServerRedirectUris);
        String redirectUri = redirectUris.length > 0 ? redirectUris[0] : "{baseUrl}/login/oauth2/code/{registrationId}"; // fallback if empty

        // Use first auth method and grant type if available
        ClientAuthenticationMethod authMethod = authMethods.length > 0
                ? new ClientAuthenticationMethod(authMethods[0])
                : new ClientAuthenticationMethod("client_secret_basic"); // default fallback

        AuthorizationGrantType grantType = grantTypes.length > 0
                ? new AuthorizationGrantType(grantTypes[0])
                : new AuthorizationGrantType("authorization_code"); // default fallback

        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(clientId)
                .clientId(clientId)
                .clientName(resourceIds)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(authMethod)
                .authorizationGrantType(grantType)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope(scopesArr)
                .authorizationUri("http://localhost:8081/oauth2/authorize")
                .tokenUri("http://localhost:8081/oauth2/token")
                .userInfoUri("http://localhost:8081/auth/userinfo") // Optional if OpenID Connect
                .userInfoAuthenticationMethod(AuthenticationMethod.HEADER)
                .userNameAttributeName(IdTokenClaimNames.SUB); // required if OpenID Connect

        if (clientId.toLowerCase().contains("google")) {
            builder
                    .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                    .tokenUri("https://oauth2.googleapis.com/token")
                    .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                    .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs");
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

}
