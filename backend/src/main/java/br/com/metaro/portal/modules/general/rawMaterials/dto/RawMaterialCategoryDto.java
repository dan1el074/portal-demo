package br.com.metaro.portal.modules.general.rawMaterials.dto;

import br.com.metaro.portal.modules.general.rawMaterials.entities.RawMaterialCategory;
import lombok.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialCategoryDto {
    private Long id;
    private String name;
    private String conversionFactor;
    private List<String> dimensionFields;
    private Instant updatedAt;

    public RawMaterialCategoryDto(RawMaterialCategory e) {
        id = e.getId();
        name = e.getName();
        conversionFactor = e.getConversionFactor();
        dimensionFields = e.getDimensionFields() == null
                ? List.of("length", "width", "thickness", "height", "weightPerSquareMeter", "litersPerUnit", "weightPerLinearMeter")
                : Arrays.stream(e.getDimensionFields().split(",")).filter(value -> !value.isBlank()).toList();
        updatedAt = e.getUpdatedAt();
    }
}
