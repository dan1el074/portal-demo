package br.com.metaro.portal.core.repositories.projections;

import java.time.LocalDate;

public interface UserConfigProjection {
    Long getPictureId();
    String getPosition();
    String getName();
    String getEmail();
    LocalDate getBirthDate();
}
