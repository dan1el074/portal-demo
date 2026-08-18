package br.com.metaro.portal.modules.general.rawMaterials.dto;

import br.com.metaro.portal.modules.general.rawMaterials.entities.RawMaterialCategory;
import lombok.*;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialCategoryDto {
    private Long id;
    private String name;
    private String conversionFactor;
    private Instant updatedAt;

    public RawMaterialCategoryDto(RawMaterialCategory e) {
        id = e.getId();
        name = e.getName();
        conversionFactor = e.getConversionFactor();
        updatedAt = e.getUpdatedAt();
    }
}
