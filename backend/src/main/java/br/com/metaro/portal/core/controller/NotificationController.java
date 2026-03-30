package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.notification.NotificationDto;
import br.com.metaro.portal.core.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> myNotifications(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return notificationService.listByUser(userId);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return Map.of("unreadCount", notificationService.countUnread(userId));
    }

    @PatchMapping("/{id}/view")
    public void markAsViewed(@PathVariable Long id, Authentication authentication) {
        Long userId = extractUserId(authentication);
        notificationService.markAsViewed(id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        Long userId = extractUserId(authentication);
        notificationService.deleteNotification(id, userId);
    }

    private Long extractUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        Object userIdClaim = jwt.getClaims().get("userId");

        return switch (userIdClaim) {
            case null -> throw new RuntimeException("Token sem userId");
            case Integer i -> i.longValue();
            case Long l -> l;
            case String s -> Long.valueOf(s);
            default -> throw new RuntimeException("userId inválido no token");
        };
    }
}
