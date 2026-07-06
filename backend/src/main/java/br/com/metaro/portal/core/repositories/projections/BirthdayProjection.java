package br.com.metaro.portal.core.repositories.projections;

import java.time.LocalDate;

public interface BirthdayProjection {
    Long getId();
    String getName();
    Long getPictureId();
    LocalDate getBirthDate();
    String getPositionName();
}
