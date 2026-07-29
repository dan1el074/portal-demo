package br.com.metaro.portal.config.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.LongSupplier;

public class RateLimitFilter extends OncePerRequestFilter {
    private static final String LIMIT_HEADER = "X-RateLimit-Limit";
    private static final String REMAINING_HEADER = "X-RateLimit-Remaining";
    private static final String RESET_HEADER = "X-RateLimit-Reset";

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;
    private final LongSupplier nanoTime;
    private final Cache<String, TokenBucket> buckets;

    public RateLimitFilter(RateLimitProperties properties, ObjectMapper objectMapper) {
        this(properties, objectMapper, System::nanoTime);
    }

    RateLimitFilter(RateLimitProperties properties, ObjectMapper objectMapper, LongSupplier nanoTime) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.nanoTime = nanoTime;

        Duration expiration = properties.getRefillPeriod().multipliedBy(2);
        if (properties.getRefillPeriod().isZero() || properties.getRefillPeriod().isNegative()) {
            throw new IllegalArgumentException("security.rate-limit.refill-period must be positive");
        }

        this.buckets = Caffeine.newBuilder()
                .maximumSize(properties.getMaxClients())
                .expireAfterAccess(expiration)
                .build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!properties.isEnabled() || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/ws/")
                || path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long now = nanoTime.getAsLong();
        String clientId = resolveClientId(request);
        TokenBucket bucket = buckets.get(clientId, ignored -> new TokenBucket(properties.getCapacity(), now));
        Decision decision = bucket.consume(now, properties.getCapacity(), properties.getRefillPeriod().toNanos());

        response.setHeader(LIMIT_HEADER, Long.toString(properties.getCapacity()));
        response.setHeader(REMAINING_HEADER, Long.toString(decision.remainingTokens()));

        if (decision.allowed()) {
            filterChain.doFilter(request, response);
            return;
        }

        long retryAfterSeconds = Math.max(1, (long) Math.ceil(decision.retryAfterNanos() / 1_000_000_000d));
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.RETRY_AFTER, Long.toString(retryAfterSeconds));
        response.setHeader(RESET_HEADER, Long.toString(retryAfterSeconds));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        body.put("error", "Limite de requisições excedido");
        body.put("path", request.getRequestURI());
        objectMapper.writeValue(response.getWriter(), body);
    }

    private String resolveClientId(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
                Object userId = jwtAuthentication.getToken().getClaim("userId");
                if (userId != null) {
                    return "user:" + userId;
                }
            }
            return "user:" + authentication.getName();
        }
        return "ip:" + request.getRemoteAddr();
    }

    private static final class TokenBucket {
        private double tokens;
        private long lastRefillNanos;

        private TokenBucket(long capacity, long now) {
            this.tokens = capacity;
            this.lastRefillNanos = now;
        }

        private synchronized Decision consume(long now, long capacity, long refillPeriodNanos) {
            long elapsed = Math.max(0, now - lastRefillNanos);
            tokens = Math.min(capacity, tokens + ((double) elapsed * capacity / refillPeriodNanos));
            lastRefillNanos = now;

            if (tokens >= 1) {
                tokens -= 1;
                return new Decision(true, (long) Math.floor(tokens), 0);
            }

            long retryAfterNanos = (long) Math.ceil((1 - tokens) * refillPeriodNanos / capacity);
            return new Decision(false, 0, retryAfterNanos);
        }
    }

    private record Decision(boolean allowed, long remainingTokens, long retryAfterNanos) {
    }
}
