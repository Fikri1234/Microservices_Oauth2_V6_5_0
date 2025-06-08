package com.project.client.model.entity;

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

import java.util.UUID;

/**
 * Created by user on 16:52 21/05/2025, 2025
 */
@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Table(name="OAUTH_CLIENT_DETAILS")
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

}
