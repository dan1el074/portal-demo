package br.com.metaro.portal.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {
    private final DefaultBearerTokenResolver headerResolver = new DefaultBearerTokenResolver();

    @Override
    public String resolve(HttpServletRequest request) {
        // O logout precisa continuar acessivel mesmo quando o cookie ja expirou,
        // para que o navegador consiga remove-lo.
        if ("POST".equals(request.getMethod()) && "/api/auth/logout".equals(request.getRequestURI())) {
            return null;
        }

        if (request.getCookies() != null) {
            String cookieToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> AuthCookieService.COOKIE_NAME.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .filter(value -> !value.isBlank())
                    .findFirst()
                    .orElse(null);
            if (cookieToken != null) {
                return cookieToken;
            }
        }

        // Mantem compatibilidade com Swagger e clientes nativos durante a migracao.
        return headerResolver.resolve(request);
    }
}
