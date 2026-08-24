package br.com.metaro.portal.modules.general.rawMaterials.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialHistoryRetentionDto {
    @NotNull
    @Min(10)
    @Max(100000)
    private Integer value;
}
