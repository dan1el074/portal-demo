package br.com.metaro.portal.core.dto.notification;

import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.core.entities.Notification;
import br.com.metaro.portal.core.repositories.projections.NotificationProjection;
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

    public NotificationDto(NotificationProjection projection) {
        this.id = projection.getId();
        this.message = projection.getMessage();
        this.actionUrl = projection.getActionUrl();
        this.viewed = projection.getViewed();
        this.autoDelete = projection.getAutoDelete();
        this.type = projection.getType();
        this.referenceId = projection.getReferenceId();
        this.createdAt = projection.getCreatedAt();

        if (projection.getCreatedById() != null) {
            this.createdBy = new UserSummaryDto(
                    projection.getCreatedById(),
                    projection.getCreatedByName(),
                    projection.getCreatedByPictureId(),
                    projection.getCreatedByPositionName()
            );
        }
    }
}
