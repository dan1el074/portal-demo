package br.com.metaro.portal.modules.general.rawMaterials.repositories.projections;

import java.math.BigDecimal;
import java.time.Instant;

public interface RawMaterialHistoryProjection {
    Long getId();
    String getAction();
    BigDecimal getPreviousStorage();
    BigDecimal getNewStorage();
    String getChangedFields();
    Instant getCreatedAt();
    String getUser();
}
