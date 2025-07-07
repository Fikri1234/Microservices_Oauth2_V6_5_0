package com.project.auth.repository;

/**
 * Created by user on 6:14 06/07/2025, 2025
 */

import com.project.auth.model.entity.OAuthClientDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Repository;

import java.util.Iterator;

@Repository
@AllArgsConstructor
public class JpaClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {

    private final OAuthClientDetailsRepository repo;

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return repo.findByClientId(registrationId)
                .map(OAuthClientDetails::toClientRegistration)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + registrationId));
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
        return repo.findAll().stream()
                .filter(entity -> entity.getWebServerRedirectUris() != null) // only external providers
                .map(OAuthClientDetails::toClientRegistration)
                .iterator();
    }
}
