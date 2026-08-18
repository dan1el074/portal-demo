package br.com.metaro.portal.modules.general.rawMaterials.repositories.projections;

import java.math.BigDecimal;
import java.time.Instant;

public interface RawMaterialListProjection {
    Long getId();
    String getCode();
    String getName();
    String getDescription();
    BigDecimal getCurrentStorage();
    BigDecimal getMinStorage();
    BigDecimal getMaxStorage();
    BigDecimal getLength();
    BigDecimal getWidth();
    BigDecimal getThickness();
    BigDecimal getWeightPerSquareMeter();
    String getConversionFactor();
    String getType();
    Boolean getActive();
    Instant getUpdateAt();
    String getUser();
}
