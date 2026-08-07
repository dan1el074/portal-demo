package br.com.metaro.portal.core.repositories.projections;

import java.time.Instant;

public interface EventProjection {
    Long getId();
    String getTitle();
    Instant getEventDate();
    Instant getUpdatedAt();
    Long getPictureId();
}
