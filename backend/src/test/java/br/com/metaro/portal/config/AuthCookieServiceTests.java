package br.com.metaro.portal.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class AuthCookieServiceTests {
    @Test
    void writesProtectedAuthenticationCookie() {
        AuthCookieService service = new AuthCookieService(true, "Strict");
        MockHttpServletResponse response = new MockHttpServletResponse();

        service.write(response, "jwt-value", Duration.ofMinutes(15));

        assertThat(response.getHeader(HttpHeaders.SET_COOKIE))
                .contains("portal_access_token=jwt-value")
                .contains("Path=/")
                .contains("Max-Age=900")
                .contains("Secure")
                .contains("HttpOnly")
                .contains("SameSite=Strict");
    }

    @Test
    void clearsAuthenticationCookie() {
        AuthCookieService service = new AuthCookieService(false, "Strict");
        MockHttpServletResponse response = new MockHttpServletResponse();

        service.clear(response);

        assertThat(response.getHeader(HttpHeaders.SET_COOKIE))
                .contains("portal_access_token=")
                .contains("Max-Age=0");
    }
}
