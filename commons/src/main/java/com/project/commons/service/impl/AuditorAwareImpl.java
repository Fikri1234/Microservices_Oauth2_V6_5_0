package com.project.commons.service.impl;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by user on 4:04 18/05/2025, 2025
 */

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public java.util.Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return java.util.Optional.of(authentication.getName());
        }
        return java.util.Optional.of("SYSTEM"); // Default value if no authentication is present
    }
}
