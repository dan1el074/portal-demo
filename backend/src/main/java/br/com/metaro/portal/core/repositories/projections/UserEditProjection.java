package br.com.metaro.portal.core.repositories.projections;

import java.time.LocalDate;

public interface UserEditProjection {
    Long getId();
    Long getPictureId();
    String getName();
    Long getPositionId();
    String getEmail();
    LocalDate getBirthDate();
    String getUsername();
    Boolean getActivated();
    String getSupportToken();
}
