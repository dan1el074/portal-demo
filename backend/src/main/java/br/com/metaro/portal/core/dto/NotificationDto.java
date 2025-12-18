package br.com.metaro.portal.core.dto;

import br.com.metaro.portal.core.entities.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationDto {
    private Long id;
    private String title;
    private Boolean isNew;
    private Instant createdAt;

    public NotificationDto(Notification entity) {
        id = entity.getId();
        title = entity.getTitle();
        isNew = entity.getIsNew();
        createdAt = entity.getCreatedAt();
    }
}
