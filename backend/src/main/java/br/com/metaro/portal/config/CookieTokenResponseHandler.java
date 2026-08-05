package br.com.metaro.portal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CookieTokenResponseHandler implements AuthenticationSuccessHandler {
    private final AuthCookieService cookieService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2AccessToken accessToken = ((OAuth2AccessTokenAuthenticationToken) authentication).getAccessToken();
        Instant expiresAt = accessToken.getExpiresAt();
        Duration maxAge = expiresAt == null
                ? Duration.ofHours(1)
                : Duration.between(Instant.now(), expiresAt).isNegative()
                    ? Duration.ZERO
                    : Duration.between(Instant.now(), expiresAt);

        cookieService.write(response, accessToken.getTokenValue(), maxAge);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                Map.of("authenticated", true, "expires_in", maxAge.toSeconds()
        ));
    }
}
