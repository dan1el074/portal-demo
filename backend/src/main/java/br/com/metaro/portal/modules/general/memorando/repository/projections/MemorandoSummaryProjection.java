package br.com.metaro.portal.modules.general.memorando.repository.projections;

public interface MemorandoSummaryProjection {
    Long getTotal();
    Long getActive();
    Long getApproved();
    Long getCanceled();
    Long getDraft();
}
