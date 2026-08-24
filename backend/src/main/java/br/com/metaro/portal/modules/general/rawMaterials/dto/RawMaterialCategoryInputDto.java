package br.com.metaro.portal.modules.general.rawMaterials.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialCategoryInputDto {
    @NotBlank @Size(max = 80)
    private String name;
    @Size(max = 1000, message = "Fator de conversão deve ter no máximo 1000 caracteres")
    private String conversionFactor;
    private List<String> dimensionFields;
    private Boolean releaseToAll = true;
}
