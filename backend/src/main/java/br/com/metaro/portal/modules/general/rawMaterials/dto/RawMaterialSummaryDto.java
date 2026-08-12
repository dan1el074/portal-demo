package br.com.metaro.portal.modules.general.rawMaterials.dto;

import br.com.metaro.portal.modules.general.rawMaterials.repositories.projections.RawMaterialSummaryProjection;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialSummaryDto {
    private long low;
    private long ok;
    private long high;

    public RawMaterialSummaryDto(RawMaterialSummaryProjection p) {
        this(p.getLow() == null ? 0 : p.getLow(),
             p.getOk() == null ? 0 : p.getOk(),
             p.getHigh() == null ? 0 : p.getHigh());
    }
}
