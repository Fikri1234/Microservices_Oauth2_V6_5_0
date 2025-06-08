package com.project.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Created by user on 2:13 27/05/2025, 2025
 */

@Component
@RequiredArgsConstructor
public class CustomDatabaseOpaqueTokenIntrospector /*implements OpaqueTokenIntrospector*/ {

//    private final OAuthClientDetailsRepository clientDetailsRepository;
//    private final RestTemplateBuilder restTemplateBuilder;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public OAuth2AuthenticatedPrincipal introspect(String token) {
//        // Choose any client that supports introspection (e.g., with proper scope)
//        OAuthClientDetails clientDetails = clientDetailsRepository.findByClientId("your-introspection-client-id")
//                .orElseThrow(() -> new OAuth2IntrospectionException("Client not found"));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.setBasicAuth(clientDetails.getClientId(), clientDetails.getClientSecret());
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("token", token);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//        try {
//            ResponseEntity<Map> response = restTemplateBuilder.build().exchange(
//                    "http://localhost:8081/oauth2/introspect",
//                    HttpMethod.POST,
//                    request,
//                    Map.class
//            );
//
//            Map<String, Object> claims = response.getBody();
//
//            if (claims == null || !Boolean.TRUE.equals(claims.get("active"))) {
//                throw new OAuth2IntrospectionException("Token is not active");
//            }
//
//            return new DefaultOAuth2AuthenticatedPrincipal(claims, extractAuthorities(claims));
//        } catch (Exception ex) {
//            throw new OAuth2IntrospectionException("Introspection request failed", ex);
//        }
//    }
//
//    private Collection<GrantedAuthority> extractAuthorities(Map<String, Object> claims) {
//        Object authorities = claims.get("authorities");
//        if (authorities instanceof Collection<?>) {
//            return ((Collection<?>) authorities).stream()
//                    .map(Object::toString)
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toList());
//        }
//        return Collections.emptyList();
//    }
}

