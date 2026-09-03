package br.com.metaro.portal.modules.engineering.trelloIntegration.dto;

import br.com.metaro.portal.modules.engineering.trelloIntegration.repository.projections.TrelloIntegrationSummaryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrelloIntegrationSummaryDto {
    private Long total;
    private Long sent;
    private Long pending;
    private Long errors;

    public TrelloIntegrationSummaryDto(TrelloIntegrationSummaryProjection projection) {
        this(projection.getTotal(), projection.getSent(), projection.getPending(), projection.getErrors());
    }
}
