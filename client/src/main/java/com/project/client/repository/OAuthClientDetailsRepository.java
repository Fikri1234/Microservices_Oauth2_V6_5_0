package com.project.client.repository;

import com.project.client.model.entity.OAuthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by user on 16:54 21/05/2025, 2025
 */

@Repository
public interface OAuthClientDetailsRepository extends JpaRepository<OAuthClientDetails, String> {
    Optional<OAuthClientDetails> findByClientId(String clientId);
}