package com.project.auth.security;

import com.project.auth.model.entity.User;
import com.project.auth.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by user on 13:46 03/07/2025, 2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FederatedLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final OAuth2TokenIssuer tokenIssuer;
    private final PasswordEncoder passwordEncoder;
    private final RegisteredClientRepository clientRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            log.warn("Not an OAuth2AuthenticationToken");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication");
            return;
        }

        OAuth2User oauth2User = oauthToken.getPrincipal();
        String email = (String) oauth2User.getAttributes().get("email");

        if (email == null) {
            log.warn("OAuth2 user missing email");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing email in user info");
            return;
        }

        log.info("External login success: {}", email);

        // Map to existing local user
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = null;
        if (optionalUser.isEmpty()) {
            user = userRepository.findByEmail(email).orElseGet(() -> {
                log.info("Creating new user for email: {}", email);
                User newUser = new User();
                newUser.setUsername(email);
                newUser.setEmail(email);
                newUser.setPassword(passwordEncoder.encode("external")); // dummy password; won't be used
                newUser.setAccountExpired(false);
                newUser.setAccountLocked(false);
                newUser.setCredentialExpired(false);
                newUser.setActive(true);
                return userRepository.save(newUser);
            });
        } else {
            user = optionalUser.get();
        }

        log.info("User gender: {}", user.getGender());

        // Issue token
        RegisteredClient client = clientRepo.findByClientId(oauthToken.getAuthorizedClientRegistrationId());
        if (client == null) {
            throw new IllegalArgumentException("Client not found: " + oauthToken.getAuthorizedClientRegistrationId());
        }

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream().map(
                authority -> new SimpleGrantedAuthority(authority.getRoleCode())).collect(Collectors.toList());

        Authentication principal = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, grantedAuthorities);

        TokenResponse tokenResponse = tokenIssuer.issueToken(response, principal, user, client);

        // Return token to client (choose strategy)
        response.setContentType("application/json");
        response.getWriter().write(
                """
                {
                  "access_token": "%s",
                  "refresh_token": "%s"
                }
                """.formatted(tokenResponse.accessToken(), tokenResponse.refreshToken())
        );
//        response.sendRedirect("http://localhost:9000/oauth2/callback"); // TODO redirect to frontend app
    }
}
