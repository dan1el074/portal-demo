package br.com.metaro.portal.core.dto.notification;

import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.core.entities.Notification;
import br.com.metaro.portal.core.entities.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationDto {
    private Long id;
    private String message;
    private String actionUrl;
    private Boolean viewed;
    private Boolean autoDelete;
    private String type;
    private Long referenceId;
    private Instant createdAt;
    private UserSummaryDto createdBy;

    public NotificationDto(Notification entity) {
        id = entity.getId();
        message = entity.getMessage();
        viewed = entity.getViewed();
        autoDelete = entity.getAutoDelete();
        createdAt = entity.getCreatedAt();

        if (entity.getType() != null) type = entity.getType().name();
        if (entity.getReferenceId() != null) referenceId = entity.getReferenceId();
        if (entity.getActionUrl() != null) actionUrl = entity.getActionUrl();
        if (entity.getCreatedBy() != null) createdBy = new UserSummaryDto(entity.getCreatedBy());
    }
}
