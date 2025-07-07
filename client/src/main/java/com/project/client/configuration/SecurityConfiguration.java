package com.project.client.configuration;

import com.project.client.repository.DatabaseClientRegistrationRepository;
import com.project.client.repository.DatabasedAuthorizedClientRepository;
import com.project.client.service.CustomOAuth2UserService;
import com.project.client.service.OAuth2AuthorizedClientServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

/**
 * Created by user on 4:11 22/05/2025, 2025
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final DatabaseClientRegistrationRepository clientRegistrationRepository;
    private final DatabasedAuthorizedClientRepository databasedAuthorizedClientRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login/**", "/login/oauth2/code/*").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Client(Customizer.withDefaults()) // Only enables client features
                .oauth2Login(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository)
                        .authorizedClientService(authorizedClientService(
                                clientRegistrationRepository,
                                databasedAuthorizedClientRepository
                        ))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .failureHandler((request, response, exception) -> {
                            exception.printStackTrace(); // or use a logger
//                            response.sendRedirect("http://localhost:8002/login?error"); // TODO Frontend login error
                        })
                        .defaultSuccessUrl("/", true) // Redirect to "/" after success
//                        .successHandler((request, response, authentication) -> {
//                            response.sendRedirect("http://localhost:8002"); // TODO Frontend Home redirection
//                        })
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestRepository(authorizationRequestRepository())
                        )
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // TODO Frontend logout
                        .permitAll()
                )
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Arrays.asList("*"));
                    configuration.setAllowedMethods(Arrays.asList("*"));
                    configuration.setAllowedHeaders(Arrays.asList("*"));
                    configuration.setAllowCredentials(true);
                    return configuration;
                }))
                .httpBasic(Customizer.withDefaults())
//                .csrf(csrf -> csrf.disable())
        ;
        return http.build();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository,
            DatabasedAuthorizedClientRepository databasedAuthorizedClientRepository
    ) {
        return new OAuth2AuthorizedClientServiceImpl(clientRegistrationRepository, databasedAuthorizedClientRepository);
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository(); // good for server-side session
    }
}
