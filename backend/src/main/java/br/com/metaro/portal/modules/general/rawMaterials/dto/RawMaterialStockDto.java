package br.com.metaro.portal.modules.general.rawMaterials.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialStockDto {
    @NotNull @PositiveOrZero
    private BigDecimal currentStorage;
}
