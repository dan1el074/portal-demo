package br.com.metaro.portal.modules.general.rawMaterials.dto;

import br.com.metaro.portal.modules.general.rawMaterials.repositories.projections.RawMaterialHistoryProjection;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialHistoryDto {
    private Long id;
    private String action;
    private BigDecimal previousStorage;
    private BigDecimal newStorage;
    private List<String> changedFields;
    private Instant createdAt;
    private String user;

    public RawMaterialHistoryDto(RawMaterialHistoryProjection p) {
        this(p.getId(), p.getAction(), p.getPreviousStorage(), p.getNewStorage(),
                parseFields(p.getChangedFields()), p.getCreatedAt(), p.getUser());
    }

    private static List<String> parseFields(String fields) {
        return fields == null || fields.isBlank() ? List.of() : Arrays.asList(fields.split("\\|"));
    }
}
