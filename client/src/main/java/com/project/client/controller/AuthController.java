package com.project.client.controller;

/**
 * Created by user on 6:29 06/07/2025, 2025
 */

import com.project.commons.controller.BaseController;
import com.project.commons.model.enums.StatusConstant;
import com.project.commons.model.response.ObjectApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController extends BaseController {

    private final OAuth2AuthorizedClientService clientService;

    @GetMapping("/api/user/token")
    public ResponseEntity<?> getToken(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                    oauth2Token.getAuthorizedClientRegistrationId(),
                    oauth2Token.getName()
            );

            log.info("Access Token: {}", oauth2Token.getAuthorizedClientRegistrationId());
            log.info("name: {}", oauth2Token.getName());

            if (client != null) {
                Map<String, Object> tokenResponse = new HashMap<>();
                tokenResponse.put("accessToken", client.getAccessToken().getTokenValue());
                tokenResponse.put("tokenType", client.getAccessToken().getTokenType().getValue());
                tokenResponse.put("expiresIn", Duration.between(
                        Instant.now(), client.getAccessToken().getExpiresAt()).getSeconds());
                tokenResponse.put("issuedAt", client.getAccessToken().getIssuedAt());
                tokenResponse.put("expiresAt", client.getAccessToken().getExpiresAt());

                if (client.getRefreshToken() != null) {
                    tokenResponse.put("refreshToken", client.getRefreshToken().getTokenValue());
                }

                tokenResponse.put("scope", String.join(" ", client.getAccessToken().getScopes()));

                return ResponseEntity.ok(responseApi(tokenResponse));
            }
        }

        ObjectApiResponse response = new ObjectApiResponse();
        response.setStatus(StatusConstant.FAILED.getEn());
        response.setMessage("No token available");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
