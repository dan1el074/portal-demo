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
        return evaluate(expression, length, width, thickness, squareMeterWeight, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public BigDecimal evaluate(String expression,
                               BigDecimal length,
                               BigDecimal width,
                               BigDecimal thickness,
                               BigDecimal squareMeterWeight,
                               BigDecimal litersPerUnit,
                               BigDecimal linearMeterWeight) {
        return evaluate(expression, length, width, thickness, BigDecimal.ZERO, squareMeterWeight, litersPerUnit,
                linearMeterWeight);
    }

    public BigDecimal evaluate(String expression,
                               BigDecimal length,
                               BigDecimal width,
                               BigDecimal thickness,
                               BigDecimal height,
                               BigDecimal squareMeterWeight,
                               BigDecimal litersPerUnit,
                               BigDecimal linearMeterWeight) {
        return RawMaterialConversionFormulaParser.evaluate(expression, length, width, thickness, height,
                squareMeterWeight, litersPerUnit, linearMeterWeight);
    }

    public void validate(String expression) {
        RawMaterialConversionFormulaParser.validate(expression);
    }
}
