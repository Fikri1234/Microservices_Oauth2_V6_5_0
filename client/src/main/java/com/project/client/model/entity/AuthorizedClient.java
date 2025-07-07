package com.project.client.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Created by user on 14:26 26/05/2025, 2025
 */

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "oauth2_authorized_client")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorizedClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String clientRegistrationId;
    String principalName;

    String accessTokenValue;
    Instant accessTokenIssuedAt;
    Instant accessTokenExpiresAt;
    String accessTokenType;
    String accessTokenScopes;

    String refreshTokenValue;
    Instant refreshTokenIssuedAt;
}
