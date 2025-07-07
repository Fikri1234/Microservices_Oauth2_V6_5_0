package com.project.auth.security;

import java.time.Instant;

/**
 * Created by user on 10:57 04/07/2025, 2025
 */
public record TokenResponse(String accessToken, Instant accessTokenExpiresAt, String refreshToken, Instant refreshTokenExpiresAt) {}

