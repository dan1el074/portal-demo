package br.com.metaro.portal.core.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationSocketMessageDto {
    String type;
    NotificationDto notification;
    Long unreadCount;
    private Long notificationId;
    private Long referenceId;
}
