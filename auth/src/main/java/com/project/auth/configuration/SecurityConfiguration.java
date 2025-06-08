package com.project.auth.configuration;

import com.project.auth.model.entity.OAuthClientDetails;
import com.project.auth.repository.DatabaseRegisteredClientRepository;
import com.project.auth.repository.OAuthClientDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by user on 5:44 19/05/2025, 2025
 */

//@Configuration
public class SecurityConfiguration {

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    @Order(2)
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/**")
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(
//                        mather ->
//                                mather
//                                        .requestMatchers(
//                                                "/swagger-ui.html",
//                                                "/swagger-ui/*",
//                                                "/auth-api-docs",
//                                                "/auth-api-docs/*")
//                                        .permitAll())
//                .authorizeHttpRequests(auth -> {
//                    auth.requestMatchers("/login").permitAll();
//                    auth.requestMatchers("/token").permitAll();
//                    auth.anyRequest().authenticated();
//                })
//                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults())
////                .authenticationProvider(authProvider())
//        ;
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }

//    @Bean
//    public DaoAuthenticationProvider authProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Bean
//    public AuthorizationServerSettings authorizationServerSettings() {
//        return AuthorizationServerSettings.builder().build();
//    }
//
//    @Bean
//    public RegisteredClientRepository registeredClientRepository() {
//        return clientDetailsRepository;
//    }



//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> {
//                    auth.requestMatchers("/login").permitAll();
//                    auth.requestMatchers("/token").permitAll();
//                    auth.anyRequest().authenticated();
//                })
//                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .userDetailsService(userDetailsService)
//                .csrf(AbstractHttpConfigurer::disable)
//                .httpBasic(Customizer.withDefaults())
//                .authenticationProvider(authProvider());
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    @Bean
//    public RegisteredClientRepository registeredClientRepository() {
//        return new JdbcRegisteredClientRepository(dataSource);
//    }
//
//    @Bean
//    public RegisteredClientRepository registeredClientRepository() {
//        return new RegisteredClientRepository() {
//            @Override
//            public void save(RegisteredClient registeredClient) {
//                // No-op
//            }
//
//            @Override
//            public RegisteredClient findById(String id) {
//                return null;
//            }
//
//            @Override
//            public RegisteredClient findByClientId(String clientId) {
//                OAuthClientDetails clientDetails = clientDetailsRepository.findById(clientId)
//                        .orElse(null);
//                if (clientDetails == null) {
//                    return null;
//                }
//                // Map OAuthClientDetails to RegisteredClient
//                return RegisteredClient.withId(UUID.randomUUID().toString())
//                        .clientId(clientDetails.getClientId())
//                        .clientSecret(clientDetails.getClientSecret())
//                        .authorizationGrantTypes(grants -> {
//                            for (String grant : clientDetails.getAuthorizedGrantTypes().split(",")) {
//                                grants.add(new AuthorizationGrantType(grant.trim()));
//                            }
//                        })
////                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
////                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
////                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//                        .redirectUri(clientDetails.getWebServerRedirectUri() != null ? clientDetails.getWebServerRedirectUri() : "")
//                        .scope(clientDetails.getScope() != null ? clientDetails.getScope() : "")
//                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
//                        .tokenSettings(TokenSettings.builder()
//                                .accessTokenTimeToLive(Duration.ofSeconds(clientDetails.getAccessTokenValidity() != null ? client.getAccessTokenValidity() : 1800))
//                                .refreshTokenTimeToLive(Duration.ofSeconds(clientDetails.getRefreshTokenValidity() != null ? client.getRefreshTokenValidity() : 3600))
//                                .build())
//                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                        .build();
//            }
//        };
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder)
//                .and()
//                .build();
//    }
//
//    // Security filter chain for the Authorization Server endpoints
//    @Bean
//    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/.well-known/**", "/oauth2/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .csrf(csrf -> csrf.disable()); // For simplicity, disable CSRF
//        return http.build();
//    }
//
//    // Security filter chain for the API
//    @Bean
//    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().authenticated()
//                )
//                .formLogin().and()
//                .httpBasic();
//        return http.build();
//    }
//
//    // Provider settings (e.g., issuer URL)
//    @Bean
//    public ProviderSettings providerSettings() {
//        return ProviderSettings.builder()
//                .issuer("http://localhost:8080")
//                .build();
//    }
//
//    // Registered clients loaded from DB
//    @Bean
//    public RegisteredClientRepository registeredClientRepository() {
//        List<OAuthClientDetails> clients = clientRepository.findAll();
//
//        List<RegisteredClient> registeredClients = clients.stream().map(client -> {
//            return RegisteredClient.withId(client.getClientId())
//                    .clientId(client.getClientId())
//                    .clientSecret(client.getClientSecret())
//                    .scope(client.getScope())
//                    .authorizedGrantTypes(grantType -> {
//                        String[] types = client.getAuthorizedGrantTypes().split(",");
//                        for (String type : types) {
//                            grantType.grantType(type.trim());
//                        }
//                    })
//                    .redirectUri(client.getRedirectUris())
//                    .build();
//        }).collect(Collectors.toList());
//
//        return new InMemoryRegisteredClientRepository(registeredClients);
//    }
}
