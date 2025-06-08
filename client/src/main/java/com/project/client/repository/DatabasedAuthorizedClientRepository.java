package com.project.client.repository;

import com.project.client.model.entity.AuthorizedClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by user on 14:28 26/05/2025, 2025
 */


public interface DatabasedAuthorizedClientRepository extends JpaRepository<AuthorizedClient, Long> {
    Optional<AuthorizedClient> findByClientRegistrationIdAndPrincipalName(String clientRegistrationId, String principalName);
    void deleteByClientRegistrationIdAndPrincipalName(String clientRegistrationId, String principalName);
}
