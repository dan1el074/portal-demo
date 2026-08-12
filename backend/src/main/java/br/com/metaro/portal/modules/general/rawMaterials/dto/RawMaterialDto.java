package br.com.metaro.portal.modules.general.rawMaterials.dto;

import br.com.metaro.portal.modules.general.rawMaterials.entities.RawMaterial;
import br.com.metaro.portal.modules.general.rawMaterials.repositories.projections.RawMaterialListProjection;
import br.com.metaro.portal.modules.general.rawMaterials.service.RawMaterialConversionFormula;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal currentStorage;
    private BigDecimal currentStorageKg;
    private BigDecimal minStorage;
    private BigDecimal minStorageKg;
    private BigDecimal maxStorage;
    private BigDecimal maxStorageKg;
    private String type;
    private Long categoryId;
    private Boolean active;
    private Instant updateAt;
    private String user;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal thickness;
    private BigDecimal weightPerSquareMeter;

    public RawMaterialDto(RawMaterialListProjection p) {
        this(p.getId(), p.getCode(), p.getName(), p.getDescription(), p.getCurrentStorage(),
            weight(p.getCurrentStorage(), p.getLength(), p.getWidth(), p.getThickness(), p.getWeightPerSquareMeter(), p.getConversionFactor()),
            p.getMinStorage(), weight(p.getMinStorage(), p.getLength(), p.getWidth(), p.getThickness(), p.getWeightPerSquareMeter(), p.getConversionFactor()),
            p.getMaxStorage(), weight(p.getMaxStorage(), p.getLength(), p.getWidth(), p.getThickness(), p.getWeightPerSquareMeter(), p.getConversionFactor()),
            p.getType(), null, p.getActive(), p.getUpdateAt(), p.getUser(), p.getLength(), p.getWidth(),
            p.getThickness(), p.getWeightPerSquareMeter());
    }

    public RawMaterialDto(RawMaterial r) {
        this(r.getId(), r.getCode(), r.getName(), r.getDescription(), r.getCurrentStorage(),
            weight(r.getCurrentStorage(), r.getLength(), r.getWidth(), r.getThickness(), r.getWeightPerSquareMeter(), r.getCategory().getConversionFactor()),
            r.getMinStorage(), weight(r.getMinStorage(), r.getLength(), r.getWidth(), r.getThickness(), r.getWeightPerSquareMeter(), r.getCategory().getConversionFactor()),
            r.getMaxStorage(), weight(r.getMaxStorage(), r.getLength(), r.getWidth(), r.getThickness(), r.getWeightPerSquareMeter(), r.getCategory().getConversionFactor()),
            r.getCategory().getName(), r.getCategory().getId(), r.getActive(), r.getUpdatedAt(),
            r.getUpdatedBy().getName(), r.getLength(), r.getWidth(), r.getThickness(), r.getWeightPerSquareMeter());
    }

    private static BigDecimal weight(
            BigDecimal quantity,
            BigDecimal length,
            BigDecimal width,
            BigDecimal thickness,
            BigDecimal sqmWeight,
            String formula
    ) {
        if (quantity == null || formula == null || formula.isBlank()) {
            return BigDecimal.ZERO.setScale(3);
        }
        try {
            BigDecimal unitWeight = RawMaterialConversionFormula.evaluate(formula, length, width, thickness, sqmWeight);
            if (unitWeight.signum() < 0) return BigDecimal.ZERO.setScale(3);
            return quantity.multiply(unitWeight).setScale(3, RoundingMode.HALF_UP);
        } catch (IllegalArgumentException | ArithmeticException exception) {
            return BigDecimal.ZERO.setScale(3);
        }
    }
}
