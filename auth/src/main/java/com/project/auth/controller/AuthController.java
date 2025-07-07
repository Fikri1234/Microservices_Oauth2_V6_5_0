package com.project.auth.controller;

import com.project.auth.constant.ServicePath;
import com.project.auth.model.dto.request.LoginRequest;
import com.project.auth.model.entity.User;
import com.project.auth.repository.UserRepository;
import com.project.auth.security.OAuth2TokenIssuer;
import com.project.auth.security.TokenResponse;
import com.project.auth.utils.CookieUtil;
import com.project.commons.controller.BaseController;
import com.project.commons.exception.AuthenticationExceptionHandler;
import com.project.commons.model.dto.response.MessageOnlyResponse;
import com.project.commons.model.enums.StatusConstant;
import com.project.commons.model.response.ObjectApiResponse;
import com.project.commons.utils.MessageUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by user on 0:31 27/05/2025, 2025
 */

@Slf4j
@RequestMapping(ServicePath.AUTH)
@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final AuthenticationManager authenticationManager;
    private final RegisteredClientRepository registeredClientRepository;
    private final CookieUtil cookieUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OAuth2TokenIssuer tokenIssuer;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request,
                                   HttpServletResponse response) {

        RegisteredClient client = registeredClientRepository.findByClientId(request.clientId());
        if (client == null || !passwordEncoder.matches(request.clientSecret(), client.getClientSecret())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client credentials");
        }

        // Step 1: Authenticate manually
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        try {
            authenticationManager.authenticate(authentication);

            User user = userRepository.findByUsername(request.username())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TokenResponse tokenResponse = tokenIssuer.issueToken(response, authentication, user, client);
            log.info("Authorization access token: {}", tokenResponse.accessToken());
            log.info("Authorization refresh token: {}", tokenResponse.refreshToken() );

            MessageOnlyResponse msg = new MessageOnlyResponse(MessageUtil.getMessage("login.success"));

            return ResponseEntity.ok(responseApi(msg));

        } catch (Exception e) {
            throw new AuthenticationExceptionHandler("Invalid credentials", e);
        }
    }

    @GetMapping("/token")
    public ResponseEntity<?> getToken(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
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


    @GetMapping("/userinfo")
    public Map<String, Object> userInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();

        return Map.of(
                "sub", authentication.getName(),
                "attributes", principal.getAttributes(),
                "authorities", principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }

    @GetMapping("/session-info")
    public Map<String, Object> sessionInfo(HttpSession session) {
        return Map.of(
                "id", session.getId(),
                "creationTime", new Date(session.getCreationTime()),
                "lastAccessedTime", new Date(session.getLastAccessedTime()),
                "maxInactiveInterval (sec)", session.getMaxInactiveInterval()
        );
    }

    @GetMapping("/tess")
    public ResponseEntity<?> testEndpoint() {
        // This is just a test endpoint to verify the controller is working
        MessageOnlyResponse msg = new MessageOnlyResponse(MessageUtil.getMessage("login.success"));

        return ResponseEntity.ok(responseApi(msg));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader, HttpServletResponse response) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationExceptionHandler("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);

        if (authorization != null) {
            authorizationService.remove(authorization);
            cookieUtil.clearCookie(response, "access_token");
            cookieUtil.clearCookie(response, "refresh_token");
        }

        return ResponseEntity.ok("Logged out successfully");
    }

}
