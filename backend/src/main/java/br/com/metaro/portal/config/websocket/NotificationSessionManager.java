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

    public void addSession(Long userId, WebSocketSession session) {
        sessionsByUser.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void removeSession(Long userId, WebSocketSession session) {
        Set<WebSocketSession> sessions = sessionsByUser.get(userId);
        if (sessions != null) {
            sessions.remove(session);

            if (sessions.isEmpty()) sessionsByUser.remove(userId);
        }
    }

    public Set<WebSocketSession> getSessions(Long userId) {
        return sessionsByUser.getOrDefault(userId, Set.of());
    }

    public void forceLogout(Long userId) {
        for (WebSocketSession session : getSessions(userId)) {
            if (!session.isOpen()) continue;
            try {
                session.sendMessage(new TextMessage("{\"type\":\"FORCE_LOGOUT\"}"));
            } catch (Exception ignored) {
                // O token já foi invalidado; o cliente será redirecionado na próxima requisição.
            }
        }
    }
}
