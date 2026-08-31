package br.com.metaro.portal.config;

import br.com.metaro.portal.config.websocket.NotificationSessionManager;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationSessionManagerTests {

    @Test
    void forceLogoutSessionNotifiesOnlyTheSelectedAuthorization() throws Exception {
        NotificationSessionManager manager = new NotificationSessionManager();
        WebSocketSession selectedSession = mock(WebSocketSession.class);
        WebSocketSession otherSession = mock(WebSocketSession.class);
        when(selectedSession.isOpen()).thenReturn(true);
        when(otherSession.isOpen()).thenReturn(true);

        manager.addSession(1L, "selected-authorization", selectedSession);
        manager.addSession(1L, "other-authorization", otherSession);

        manager.forceLogoutSession("selected-authorization");

        verify(selectedSession).sendMessage(argThat(message ->
                message instanceof TextMessage textMessage
                        && textMessage.getPayload().contains("FORCE_LOGOUT")
        ));
        verify(otherSession, never()).sendMessage(any());
    }
}
