package com.project.auth.model.dto.request;

/**
 * Created by user on 4:05 06/07/2025, 2025
 */
public record LoginRequest(String username, String password, String clientId, String clientSecret) {}
