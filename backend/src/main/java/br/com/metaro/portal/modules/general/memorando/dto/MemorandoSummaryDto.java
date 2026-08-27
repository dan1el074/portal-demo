package br.com.metaro.portal.modules.general.memorando.dto;

import br.com.metaro.portal.modules.general.memorando.repository.projections.MemorandoSummaryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemorandoSummaryDto {
    private Long total;
    private Long active;
    private Long approved;
    private Long canceled;
    private Long draft;

    public MemorandoSummaryDto(MemorandoSummaryProjection projection) {
        this(projection.getTotal(), projection.getActive(), projection.getApproved(), projection.getCanceled(), projection.getDraft());
    }
}
