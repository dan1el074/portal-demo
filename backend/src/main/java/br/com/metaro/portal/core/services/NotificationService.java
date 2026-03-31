package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.notification.NotificationDto;
import br.com.metaro.portal.core.dto.notification.NotificationSocketMessageDto;
import br.com.metaro.portal.core.entities.Notification;
import br.com.metaro.portal.core.entities.NotificationType;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.NotificationRepository;
import br.com.metaro.portal.websocket.NotificationSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSessionManager sessionManager;
    private final ObjectMapper objectMapper;

    @Transactional
    public void create(
            String message,
            String actionUrl,
            Boolean autoDelete,
            NotificationType type,
            Long referenceId,
            User createdBy,
            User targetUser
    ) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setActionUrl(actionUrl);
        notification.setViewed(false);
        notification.setAutoDelete(autoDelete);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setCreatedAt(Instant.now());
        notification.setUser(targetUser);
        notification.setCreatedBy(createdBy);
        notification = notificationRepository.save(notification);

        NotificationDto dto = new NotificationDto(notification);
        sendNewNotificationToUser(targetUser.getId(), dto);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> listByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationDto::new)
                .toList();
    }

    @Transactional
    public List<Notification> findByReferenceIdAndType(Long referenceId, NotificationType type) {
        return notificationRepository.findByReferenceIdAndType(referenceId, type);
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndViewedFalse(userId);
    }

    @Transactional
    public void markAsViewed(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        if (notification.getViewed().equals(false)) {
            notification.setViewed(true);
            notificationRepository.save(notification);
        }

        sendNotificationViewedToUser(userId, notificationId);
    }

    @Transactional
    public void delete(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        notificationRepository.delete(notification);
        sendNotificationRemovedToUser(userId, notificationId);
    }

    private void sendNewNotificationToUser(Long userId, NotificationDto notificationDTO) {
        long unreadCount = notificationRepository.countByUserIdAndViewedFalse(userId);

        NotificationSocketMessageDto payload = new NotificationSocketMessageDto(
                "NEW_NOTIFICATION",
                notificationDTO,
                unreadCount,
                null,
                null
        );

        sendToUserSessions(userId, payload, "Erro ao enviar notificação via websocket");
    }

    private void sendNotificationViewedToUser(Long userId, Long notificationId) {
        long unreadCount = notificationRepository.countByUserIdAndViewedFalse(userId);

        NotificationSocketMessageDto payload = new NotificationSocketMessageDto(
                "NOTIFICATION_VIEWED",
                null,
                unreadCount,
                notificationId,
                null
        );

        sendToUserSessions(userId, payload, "Erro ao enviar atualização de visualização via websocket");
    }

    private void sendNotificationRemovedToUser(Long userId, Long notificationId) {
        long unreadCount = notificationRepository.countByUserIdAndViewedFalse(userId);

        NotificationSocketMessageDto payload = new NotificationSocketMessageDto(
                "NOTIFICATION_REMOVED",
                null,
                unreadCount,
                notificationId,
                null
        );

        sendToUserSessions(userId, payload, "Erro ao enviar remoção de notificação via websocket");
    }

    private void sendToUserSessions(Long userId, NotificationSocketMessageDto payload, String errorMessage) {
        try {
            String json = objectMapper.writeValueAsString(payload);

            for (WebSocketSession session : sessionManager.getSessions(userId)) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(errorMessage, e);
        }
    }
}
