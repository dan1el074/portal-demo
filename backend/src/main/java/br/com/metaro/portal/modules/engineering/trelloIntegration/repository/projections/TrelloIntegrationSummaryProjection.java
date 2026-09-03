package br.com.metaro.portal.modules.engineering.trelloIntegration.repository.projections;

public interface TrelloIntegrationSummaryProjection {
    Long getTotal();
    Long getSent();
    Long getPending();
    Long getErrors();
}
