package br.com.metaro.portal.config;

import br.com.metaro.portal.core.dto.auth.ActiveSessionDto;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

public class ActiveSessionAuthorizationService implements OAuth2AuthorizationService {
    private final InMemoryOAuth2AuthorizationService delegate = new InMemoryOAuth2AuthorizationService();
    private final Map<String, OAuth2Authorization> authorizations = new ConcurrentHashMap<>();
    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    @Override
    public void save(OAuth2Authorization authorization) {
        delegate.save(authorization);
        authorizations.put(authorization.getId(), authorization);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        delegate.remove(authorization);
        authorizations.remove(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        return delegate.findByToken(token, tokenType);
    }

    public List<ActiveSessionDto> findActiveSessions() {
        authorizations.entrySet().removeIf(entry -> {
            var accessToken = entry.getValue().getAccessToken();
            return accessToken == null || !accessToken.isActive();
        });

        return authorizations.values().stream()
                .map(OAuth2Authorization::getAccessToken)
                .filter(accessToken -> accessToken != null && accessToken.isActive())
                .map(accessToken -> {
                    Object userIdClaim = Objects.requireNonNull(accessToken.getClaims()).get("userId");
                    Object usernameClaim = accessToken.getClaims().get("username");
                    if (userIdClaim == null || usernameClaim == null) return null;

                    try {
                        Long userId = userIdClaim instanceof Number number
                                ? number.longValue()
                                : Long.parseLong(String.valueOf(userIdClaim));
                        return new ActiveSessionDto(
                                authorizationId(accessToken),
                                userId,
                                String.valueOf(usernameClaim),
                                accessToken.getToken().getIssuedAt(),
                                accessToken.getToken().getExpiresAt()
                        );
                    } catch (NumberFormatException ignored) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted((first, second) -> second.getIssuedAt().compareTo(first.getIssuedAt()))
                .toList();
    }

    public Optional<Long> disconnect(String authorizationId) {
        OAuth2Authorization authorization = authorizations.get(authorizationId);
        if (authorization == null || authorization.getAccessToken() == null) return Optional.empty();
        var accessToken = authorization.getAccessToken();
        Object userIdClaim = Objects.requireNonNull(accessToken.getClaims()).get("userId");
        revokedTokens.put(accessToken.getToken().getTokenValue(), accessToken.getToken().getExpiresAt());
        remove(authorization);
        try {
            return Optional.of(userIdClaim instanceof Number number
                    ? number.longValue()
                    : Long.parseLong(String.valueOf(userIdClaim)));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public void disconnectToken(String token) {
        if (token == null || token.isBlank()) return;

        OAuth2Authorization authorization = delegate.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (authorization == null) return;
        disconnect(authorization.getId());
    }

    public Optional<String> findAuthorizationIdByToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();

        return Optional.ofNullable(delegate.findByToken(token, OAuth2TokenType.ACCESS_TOKEN))
                .map(OAuth2Authorization::getId);
    }

    public boolean disconnectUser(Long userId) {
        List<OAuth2Authorization> userAuthorizations = authorizations.values().stream()
                .filter(authorization -> authorization.getAccessToken() != null)
                .filter(authorization -> {
                    Object claim = Objects.requireNonNull(authorization.getAccessToken().getClaims()).get("userId");
                    if (claim == null) return false;
                    try {
                        long value = claim instanceof Number number
                                ? number.longValue()
                                : Long.parseLong(String.valueOf(claim));
                        return value == userId;
                    } catch (NumberFormatException exception) {
                        return false;
                    }
                })
                .toList();

        for (OAuth2Authorization authorization : userAuthorizations) {
            var accessToken = authorization.getAccessToken();
            revokedTokens.put(accessToken.getToken().getTokenValue(), accessToken.getToken().getExpiresAt());
            remove(authorization);
        }
        return !userAuthorizations.isEmpty();
    }

    public boolean isRevoked(String token) {
        Instant now = Instant.now();
        revokedTokens.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isBefore(now));
        return revokedTokens.containsKey(token);
    }

    private String authorizationId(OAuth2Authorization.Token<?> accessToken) {
        return authorizations.values().stream()
                .filter(authorization -> authorization.getAccessToken() == accessToken)
                .map(OAuth2Authorization::getId)
                .findFirst()
                .orElseThrow();
    }
}
