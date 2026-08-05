package br.com.metaro.portal.config;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class CookieBearerTokenResolverTests {
    private final CookieBearerTokenResolver resolver = new CookieBearerTokenResolver();

    @Test
    void resolvesTokenFromHttpOnlyCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user/me");
        request.setCookies(new Cookie(AuthCookieService.COOKIE_NAME, "cookie-token"));

        assertThat(resolver.resolve(request)).isEqualTo("cookie-token");
    }

    @Test
    void keepsAuthorizationHeaderCompatibilityWhenCookieIsAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user/me");
        request.addHeader("Authorization", "Bearer header-token");

        assertThat(resolver.resolve(request)).isEqualTo("header-token");
    }

    @Test
    void ignoresExpiredCredentialsOnLogoutSoCookieCanBeCleared() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/logout");
        request.setCookies(new Cookie(AuthCookieService.COOKIE_NAME, "expired-token"));

        assertThat(resolver.resolve(request)).isNull();
    }
}
