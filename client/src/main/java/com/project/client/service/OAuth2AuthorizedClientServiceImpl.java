package com.project.client.service;

import com.project.client.model.entity.AuthorizedClient;
import com.project.client.repository.DatabasedAuthorizedClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

/**
 * Created by user on 13:36 26/05/2025, 2025
 */

@RequiredArgsConstructor
public class OAuth2AuthorizedClientServiceImpl implements OAuth2AuthorizedClientService {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final DatabasedAuthorizedClientRepository repository;

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {

        Optional<AuthorizedClient> optional = repository.findByClientRegistrationIdAndPrincipalName(
                clientRegistrationId, principalName
        );

        if (optional.isEmpty()) return null;

        AuthorizedClient entity = optional.get();
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                entity.getAccessTokenValue(),
                entity.getAccessTokenIssuedAt(),
                entity.getAccessTokenExpiresAt(),
                new HashSet<>(Arrays.asList(entity.getAccessTokenScopes().split(",")))
        );

        OAuth2RefreshToken refreshToken = null;
        if (entity.getRefreshTokenValue() != null) {
            refreshToken = new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    entity.getRefreshTokenIssuedAt()
            );
        }

        return (T) new OAuth2AuthorizedClient(registration, principalName, accessToken, refreshToken);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        ClientRegistration registration = authorizedClient.getClientRegistration();

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        AuthorizedClient entity = repository.findByClientRegistrationIdAndPrincipalName(
                registration.getRegistrationId(), principal.getName()
        ).orElse(new AuthorizedClient());

        entity.setClientRegistrationId(registration.getRegistrationId());
        entity.setPrincipalName(principal.getName());

        entity.setAccessTokenValue(accessToken.getTokenValue());
        entity.setAccessTokenIssuedAt(accessToken.getIssuedAt());
        entity.setAccessTokenExpiresAt(accessToken.getExpiresAt());
        entity.setAccessTokenType(accessToken.getTokenType().getValue());
        entity.setAccessTokenScopes(String.join(",", accessToken.getScopes()));

        if (refreshToken != null) {
            entity.setRefreshTokenValue(refreshToken.getTokenValue());
            entity.setRefreshTokenIssuedAt(refreshToken.getIssuedAt());
        }

        repository.save(entity);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        repository.deleteByClientRegistrationIdAndPrincipalName(clientRegistrationId, principalName);
    }
}
