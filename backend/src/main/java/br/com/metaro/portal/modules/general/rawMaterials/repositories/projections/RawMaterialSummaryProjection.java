package br.com.metaro.portal.modules.general.rawMaterials.repositories.projections;

public interface RawMaterialSummaryProjection {
    Long getLow();
    Long getOk();
    Long getHigh();
}
