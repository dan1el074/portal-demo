package br.com.metaro.portal.modules.general.stepFlow.repositories.projections;

import br.com.metaro.portal.modules.general.stepFlow.entities.StepType;

public interface StepCountProjection {
    StepType getStep();
    Long getCount();
}
