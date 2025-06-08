package com.project.auth.configuration;

import com.project.auth.repository.DatabaseRegisteredClientRepository;
import com.project.auth.repository.OAuthClientDetailsRepository;
import com.project.auth.security.DatabaseOpaqueTokenIntrospector;
import com.project.auth.security.DynamicOpaqueTokenIntrospector;
import com.project.auth.security.OAuth2OpaqueTokenIntrospector;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.Collections;

/**
 * Created by user on 6:48 19/05/2025, 2025
 */

@Configuration
@EnableWebSecurity
public class AuthorizationConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                          DatabaseOpaqueTokenIntrospector introspector) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers("/login", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .opaqueToken(token -> token.introspector(introspector)))
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http,
                                                             DatabaseOpaqueTokenIntrospector introspector) throws Exception {
//        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
//                new OAuth2AuthorizationServerConfigurer();

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, authorizationServer -> {
                    // No .oidc() to avoid JWT
                })
                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers("/userinfo").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling((exceptions) ->

                        exceptions.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"), new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .opaqueToken(token -> token.introspector(introspector)))
                .httpBasic(Customizer.withDefaults())
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session
//                        .sessionFixation().newSession()
//                        .maximumSessions(1)) // remove this line if you want to allow multiple sessions per user
        ;

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            OAuthClientDetailsRepository repo, PasswordEncoder passwordEncoder) {
        return new DatabaseRegisteredClientRepository(repo, passwordEncoder);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().issuer("http://localhost:8081").build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }






//    @Bean
//    public OAuth2TokenIntrospectionAuthenticationProvider tokenIntrospectionAuthenticationProvider(
//            OAuth2AuthorizationService authorizationService,
//            RegisteredClientRepository registeredClientRepository) {
//
//        OAuth2TokenIntrospectionAuthenticationProvider provider =
//                new OAuth2TokenIntrospectionAuthenticationProvider(registeredClientRepository, authorizationService);
//
//        return provider;
//    }
//
//    @Bean
//    public OpaqueTokenIntrospector tokenIntrospector(
//            OAuth2AuthorizationService authorizationService,
//            RegisteredClientRepository registeredClientRepository) {
//
//        return new OAuth2TokenIntrospector() {
//            private final OAuth2TokenIntrospectionAuthenticationProvider delegate =
//                    new OAuth2TokenIntrospectionAuthenticationProvider(authorizationService, registeredClientRepository);
//
//            @Override
//            public OAuth2AuthenticatedPrincipal introspect(String token) {
//                // This is where Spring Security calls to validate access token
//                OAuth2TokenIntrospectionAuthenticationToken authRequest =
//                        new OAuth2TokenIntrospectionAuthenticationToken(token, null, null);
//                Authentication authResult = delegate.authenticate(authRequest);
//                return (OAuth2AuthenticatedPrincipal) authResult.getPrincipal();
//            }
//        };
//    }







//    @Bean
//    @Order(2)
//    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests((authorize) -> authorize
////                        .requestMatchers("/login", "/login/oauth2/**").permitAll()
//                                .anyRequest().authenticated()
//                )
//                // Form login handles the redirect to the login page from the
//                // authorization server filter chain
//                .formLogin(Customizer.withDefaults());
//
//        return http.build();
//    }
//
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http,
//                                                             OpaqueTokenIntrospector introspector) throws Exception {
//        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
//                OAuth2AuthorizationServerConfigurer.authorizationServer().oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0
//
//        http
//                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
//                .with(authorizationServerConfigurer, (authorizationServer) ->
//                        authorizationServer
//                                .oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
//                )
//                .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
//                .exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor(
//                        new LoginUrlAuthenticationEntryPoint("/login"), new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
////                .oauth2ResourceServer(resourceServer -> resourceServer
////                        .opaqueToken(token -> token.introspector(tokenIntrospector()))
////                )
////                .oauth2ResourceServer(oauth2 ->
////                        oauth2.opaqueToken(opaque -> opaque
////                                .introspector(introspector(authorizationService, registeredClientRepository, clientRepo))
////                        )
////                )
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .opaqueToken(token -> token.introspector(introspector))
//                )
//                .csrf(csrf -> csrf.disable())
//        ;
//
//        return http.build();
//    }
//
//    @Bean
//    public RegisteredClientRepository registeredClientRepository(
//            OAuthClientDetailsRepository repo, PasswordEncoder encoder) {
//        return new DatabaseRegisteredClientRepository(repo, encoder);
//    }
//
//    @Bean
//    public AuthorizationServerSettings authorizationServerSettings() {
//        return AuthorizationServerSettings.builder().build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public RestTemplateBuilder restTemplateBuilder() {
//        return new RestTemplateBuilder();
//    }

}
