package br.com.metaro.portal.core.repositories.projections;

import br.com.metaro.portal.core.entities.NotificationType;

public interface NotificationMinProjection {
    NotificationType getType();
    Long getReferenceId();
}
