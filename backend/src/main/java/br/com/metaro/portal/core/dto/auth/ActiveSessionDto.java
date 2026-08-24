package br.com.metaro.portal.core.dto.auth;

import java.time.Instant;

public record ActiveSessionDto(
        String sessionId,
        Long userId,
        String username,
        Instant issuedAt,
        Instant expiresAt
) {}
