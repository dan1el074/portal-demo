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
    public void create(String message, String actionUrl, Boolean autoDelete, NotificationType type, Long referenceId,
                       User createdBy, User targetUser) {
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

        NotificationDto dto = toDTO(notification);
        sendNotificationToUser(targetUser.getId(), dto);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> listByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndViewedFalse(userId);
    }

    @Transactional
    public void markAsViewed(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        notification.setViewed(true);
        notificationRepository.save(notification);

        sendUnreadCountToUser(userId);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        notificationRepository.delete(notification);

        sendUnreadCountToUser(userId);
    }

    private void sendNotificationToUser(Long userId, NotificationDto notificationDTO) {
        try {
            long unreadCount = notificationRepository.countByUserIdAndViewedFalse(userId);

            NotificationSocketMessageDto payload = new NotificationSocketMessageDto(
                    "NEW_NOTIFICATION",
                    notificationDTO,
                    unreadCount
            );

            String json = objectMapper.writeValueAsString(payload);

            for (WebSocketSession session : sessionManager.getSessions(userId)) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar notificação via websocket", e);
        }
    }

    private void sendUnreadCountToUser(Long userId) {
        try {
            long unreadCount = notificationRepository.countByUserIdAndViewedFalse(userId);

            NotificationSocketMessageDto payload = new NotificationSocketMessageDto(
                    "UNREAD_COUNT_UPDATED",
                    null,
                    unreadCount
            );

            String json = objectMapper.writeValueAsString(payload);

            for (WebSocketSession session : sessionManager.getSessions(userId)) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar contador via websocket", e);
        }
    }

    private NotificationDto toDTO(Notification n) {
        return new NotificationDto(n);
    }
}
