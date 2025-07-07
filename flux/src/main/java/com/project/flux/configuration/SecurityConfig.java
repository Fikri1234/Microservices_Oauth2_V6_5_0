package com.project.flux.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

/**
 * Created by user on 10:56 16/05/2025, 2025
 */

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.build();
    }

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(Customizer.withDefaults())
                )
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .opaqueToken(opaqueToken -> opaqueToken
//                                .introspectionUri("https://idp.example.com/introspect")
//                                .introspectionClientCredentials("client", "secret")
//                        )
//                )

        ;
        return http.build();
    }

    @Bean
    public OpaqueTokenIntrospector opaqueTokenIntrospector(RestTemplate restTemplate) {
        return new SpringOpaqueTokenIntrospector(
                "http://localhost:8081/oauth2/introspect", // Authorization server introspection endpoint
                restTemplate
        );
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    private final SessionService sessionService;

    public SecurityConfig(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .pathMatchers("/public/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .csrf().disable()
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(new ReactiveAuthenticationManager() {
            @Override
            public Mono<Authentication> authenticate(Authentication authentication) {
                String token = authentication.getCredentials().toString();
                return sessionService.validateToken(token)
                        .filter(isValid -> isValid)
                        .map(isValid -> authentication)
                        .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid Token")));
            }
        });

        filter.setServerAuthenticationConverter(serverAuthenticationConverter());
        return filter;
    }

    @Bean
    public ServerAuthenticationConverter serverAuthenticationConverter() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> authHeader.substring(7))
                .map(token -> new UsernamePasswordAuthenticationToken(null, token));
    }
}
