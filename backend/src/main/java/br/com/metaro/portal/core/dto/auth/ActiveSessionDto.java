package br.com.metaro.portal.core.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ActiveSessionDto {
    private String sessionId;
    private Long userId;
    private String username;
    private Instant issuedAt;
    private Instant expiresAt;
}
