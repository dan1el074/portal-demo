package br.com.metaro.portal.modules.general.rawMaterials.service;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class RawMaterialConversionFormula {
    public BigDecimal evaluate(String expression,
                               BigDecimal length,
                               BigDecimal width,
                               BigDecimal thickness,
                               BigDecimal squareMeterWeight) {
        return RawMaterialConversionFormulaParser.evaluate(expression, length, width, thickness, squareMeterWeight);
    }

    public void validate(String expression) {
        RawMaterialConversionFormulaParser.validate(expression);
    }
}
