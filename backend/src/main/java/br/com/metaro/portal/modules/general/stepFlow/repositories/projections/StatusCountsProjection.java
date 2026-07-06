package br.com.metaro.portal.modules.general.stepFlow.repositories.projections;

public interface StatusCountsProjection {
    Long getTotalCount();
    Long getProgressCount();
    Long getCompleteCount();
    Long getLateCount();
}
