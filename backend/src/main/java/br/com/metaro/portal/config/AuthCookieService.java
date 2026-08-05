package br.com.metaro.portal.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookieService {
    public static final String COOKIE_NAME = "portal_access_token";

    private final boolean secure;
    private final String sameSite;

    public AuthCookieService(
            @Value("${security.cookie.secure:false}") boolean secure,
            @Value("${security.cookie.same-site:Strict}") String sameSite
    ) {
        this.secure = secure;
        this.sameSite = sameSite;
    }

    public void write(HttpServletResponse response, String token, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clear(HttpServletResponse response) {
        write(response, "", Duration.ZERO);
    }
}
