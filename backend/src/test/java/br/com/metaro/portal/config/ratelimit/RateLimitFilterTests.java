package br.com.metaro.portal.config.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitFilterTests {
    @Test
    void rejectsRequestsAfterClientConsumesCapacity() throws Exception {
        AtomicLong time = new AtomicLong();
        RateLimitFilter filter = filter(2, Duration.ofMinutes(1), time);

        MockHttpServletResponse first = execute(filter, "192.0.2.1", "/api/info", "GET");
        MockHttpServletResponse second = execute(filter, "192.0.2.1", "/api/info", "GET");
        MockHttpServletResponse rejected = execute(filter, "192.0.2.1", "/api/info", "GET");

        assertThat(first.getStatus()).isEqualTo(200);
        assertThat(second.getStatus()).isEqualTo(200);
        assertThat(rejected.getStatus()).isEqualTo(429);
        assertThat(rejected.getHeader("Retry-After")).isEqualTo("30");
        assertThat(rejected.getHeader("X-RateLimit-Limit")).isEqualTo("2");
        assertThat(rejected.getHeader("X-RateLimit-Remaining")).isEqualTo("0");
        assertThat(rejected.getContentAsString()).contains("Limite de requisições excedido");
    }

    @Test
    void refillsTokensOverTimeAndKeepsClientsIndependent() throws Exception {
        AtomicLong time = new AtomicLong();
        RateLimitFilter filter = filter(1, Duration.ofSeconds(10), time);

        assertThat(execute(filter, "192.0.2.1", "/api/info", "GET").getStatus()).isEqualTo(200);
        assertThat(execute(filter, "192.0.2.1", "/api/info", "GET").getStatus()).isEqualTo(429);
        assertThat(execute(filter, "192.0.2.2", "/api/info", "GET").getStatus()).isEqualTo(200);

        time.addAndGet(Duration.ofSeconds(10).toNanos());

        assertThat(execute(filter, "192.0.2.1", "/api/info", "GET").getStatus()).isEqualTo(200);
    }

    @Test
    void ignoresPreflightAndDocumentationRequests() throws Exception {
        AtomicLong time = new AtomicLong();
        RateLimitFilter filter = filter(1, Duration.ofMinutes(1), time);

        assertThat(execute(filter, "192.0.2.1", "/api/info", "OPTIONS").getStatus()).isEqualTo(200);
        assertThat(execute(filter, "192.0.2.1", "/v3/api-docs", "GET").getStatus()).isEqualTo(200);
        assertThat(execute(filter, "192.0.2.1", "/api/info", "GET").getStatus()).isEqualTo(200);
    }

    @Test
    void appliesIndependentLimitsToAuthenticatedUsersOnTheSameIp() throws Exception {
        AtomicLong time = new AtomicLong();
        RateLimitFilter filter = filter(1, Duration.ofMinutes(1), time);

        assertThat(executeAsUser(filter, 10L, "192.0.2.1").getStatus()).isEqualTo(200);
        assertThat(executeAsUser(filter, 10L, "192.0.2.1").getStatus()).isEqualTo(429);
        assertThat(executeAsUser(filter, 20L, "192.0.2.1").getStatus()).isEqualTo(200);
        assertThat(executeAsUser(filter, 10L, "192.0.2.2").getStatus()).isEqualTo(429);
    }

    private RateLimitFilter filter(long capacity, Duration period, AtomicLong time) {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setCapacity(capacity);
        properties.setRefillPeriod(period);
        return new RateLimitFilter(properties, new ObjectMapper(), time::get);
    }

    private MockHttpServletResponse execute(
            RateLimitFilter filter,
            String remoteAddress,
            String path,
            String method
    ) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setRemoteAddr(remoteAddress);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }

    private MockHttpServletResponse executeAsUser(
            RateLimitFilter filter,
            long userId,
            String remoteAddress
    ) throws Exception {
        Jwt jwt = Jwt.withTokenValue("token-" + userId)
                .header("alg", "none")
                .subject("user-" + userId)
                .claim("userId", userId)
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
        try {
            return execute(filter, remoteAddress, "/api/info", "GET");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
