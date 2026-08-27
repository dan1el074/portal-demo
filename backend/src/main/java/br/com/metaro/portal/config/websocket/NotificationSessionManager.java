package br.com.metaro.portal.config.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationSessionManager {

    private final Map<Long, Set<WebSocketSession>> sessionsByUser = new ConcurrentHashMap<>();
    private final Map<String, Set<WebSocketSession>> sessionsByAuthorization = new ConcurrentHashMap<>();

    public void addSession(Long userId, String authorizationId, WebSocketSession session) {
        sessionsByUser.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
        if (authorizationId != null) {
            sessionsByAuthorization.computeIfAbsent(authorizationId, key -> ConcurrentHashMap.newKeySet()).add(session);
        }
    }

    public void removeSession(Long userId, String authorizationId, WebSocketSession session) {
        Set<WebSocketSession> sessions = sessionsByUser.get(userId);
        if (sessions != null) {
            sessions.remove(session);

            if (sessions.isEmpty()) sessionsByUser.remove(userId);
        }

        if (authorizationId == null) return;
        Set<WebSocketSession> authorizationSessions = sessionsByAuthorization.get(authorizationId);
        if (authorizationSessions != null) {
            authorizationSessions.remove(session);
            if (authorizationSessions.isEmpty()) sessionsByAuthorization.remove(authorizationId);
        }
    }

    public Set<WebSocketSession> getSessions(Long userId) {
        return sessionsByUser.getOrDefault(userId, Set.of());
    }

    public void forceLogout(Long userId) {
        sendForceLogout(getSessions(userId));
    }

    public void forceLogoutSession(String authorizationId) {
        sendForceLogout(sessionsByAuthorization.getOrDefault(authorizationId, Set.of()));
    }

    private void sendForceLogout(Set<WebSocketSession> sessions) {
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) continue;
            try {
                session.sendMessage(new TextMessage("{\"type\":\"FORCE_LOGOUT\"}"));
            } catch (Exception ignored) {
                // O token já foi invalidado; o cliente será redirecionado na próxima requisição.
            }
        }
    }
}
