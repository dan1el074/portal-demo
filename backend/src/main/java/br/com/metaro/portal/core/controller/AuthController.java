package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.config.AuthCookieService;
import br.com.metaro.portal.config.ActiveSessionAuthorizationService;
import br.com.metaro.portal.core.dto.auth.ActiveSessionDto;
import br.com.metaro.portal.config.websocket.NotificationSessionManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthCookieService cookieService;
    private final ActiveSessionAuthorizationService authorizationService;
    private final NotificationSessionManager notificationSessionManager;

    @GetMapping("/active-sessions")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SYSTEM_PARAMS')")
    public ResponseEntity<List<ActiveSessionDto>> activeSessions() {
        return ResponseEntity.ok(authorizationService.findActiveSessions());
    }

    @DeleteMapping("/active-sessions/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> disconnectSession(@PathVariable Long userId) {
        if (authorizationService.disconnectUser(userId)) notificationSessionManager.forceLogout(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/active-sessions/{sessionId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SYSTEM_PARAMS')")
    public ResponseEntity<Void> disconnectSession(@PathVariable String sessionId) {
        authorizationService.disconnect(sessionId)
                .ifPresent(userId -> notificationSessionManager.forceLogoutSession(sessionId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(cookie -> AuthCookieService.COOKIE_NAME.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .filter(value -> value != null && !value.isBlank())
                    .findFirst()
                    .ifPresent(authorizationService::disconnectToken);
        }
        cookieService.clear(response);
        return ResponseEntity.noContent().build();
    }
}
