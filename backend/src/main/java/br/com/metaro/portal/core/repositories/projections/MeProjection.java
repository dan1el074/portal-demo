package br.com.metaro.portal.core.repositories.projections;

import java.time.LocalDate;

public interface MeProjection {
    Long getId();
    String getName();
    String getEmail();
    String getPosition();
    LocalDate getBirthDate();
    Long getPictureId();
    Boolean getActivated();
    String getUsername();
    String getSupportToken();
}
