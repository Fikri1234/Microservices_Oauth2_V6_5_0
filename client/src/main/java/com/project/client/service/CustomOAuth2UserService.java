package com.project.client.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by user on 23:49 26/05/2025, 2025
 */
@Component
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final RestOperations restOperations;

    public CustomOAuth2UserService() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        System.out.println("Client Registration ID: " + clientRegistration.getRegistrationId());
        log.info("Client Registration ID: " + clientRegistration.getRegistrationId());
        String userInfoUri = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();
        System.out.println("UserInfo URI: " + userInfoUri);
        log.info("UserInfo URI: {}", userInfoUri);
        if (userInfoUri == null || userInfoUri.isEmpty()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("missing_user_info_uri"),
                    "Missing UserInfo URI in ClientRegistration: " + clientRegistration.getRegistrationId());
        }
        System.out.println("token value: " + userRequest.getAccessToken().getTokenValue());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        try {
            RequestEntity<?> request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(userInfoUri));
            ResponseEntity<Map<String, Object>> response = restOperations.exchange(
                    request, new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> userAttributes = response.getBody();
            System.out.println("User Attributes: " + userAttributes);
            if (userAttributes == null) {
                throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info"),
                        "UserInfo response is empty for client: " + clientRegistration.getRegistrationId());
            }

            List<String> roles = (List<String>) userAttributes.getOrDefault("roles", List.of("ROLE_USER"));

            Set<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            String userNameAttributeName = clientRegistration
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();

            if (!userAttributes.containsKey(userNameAttributeName)) {
                throw new OAuth2AuthenticationException(new OAuth2Error("missing_user_attribute"),
                        "Missing user attribute '" + userNameAttributeName + "' in response.");
            }

            return new DefaultOAuth2User(authorities, userAttributes, userNameAttributeName);
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(new OAuth2Error("user_info_retrieval_error"),
                    "Failed to retrieve or parse user info: " + ex.getMessage(), ex);
        }
    }
}
