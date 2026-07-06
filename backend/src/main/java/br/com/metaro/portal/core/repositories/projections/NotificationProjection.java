package br.com.metaro.portal.core.repositories.projections;

import java.time.Instant;

public interface NotificationProjection {
    Long getId();
    String getMessage();
    String getActionUrl();
    Boolean getViewed();
    Boolean getAutoDelete();
    String getType();
    Long getReferenceId();
    Instant getCreatedAt();
    Long getCreatedById();
    String getCreatedByName();
    Long getCreatedByPictureId();
    String getCreatedByPositionName();
}
