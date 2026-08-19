package br.com.metaro.portal.modules.general.rawMaterials.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawMaterialConversionFormulaTests {
    @Test
    void evaluatesConfiguredVariablesAndOperatorPrecedence() {
        BigDecimal result = RawMaterialConversionFormula.evaluate(
                "(%c * %l * %p) / 2",
                new BigDecimal("3"),
                new BigDecimal("2"),
                new BigDecimal("4.75"),
                new BigDecimal("10")
        );

        assertThat(result).isEqualByComparingTo("30");
    }

    @Test
    void supportsThicknessAndDecimalComma() {
        BigDecimal result = RawMaterialConversionFormula.evaluate(
                "%e * 1,5",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("4"),
                BigDecimal.ZERO
        );

        assertThat(result).isEqualByComparingTo("6");
    }

    @Test
    void supportsLitersPerUnitAndLinearMeterWeight() {
        BigDecimal result = RawMaterialConversionFormula.evaluate(
                "%u + (%c * %m)", BigDecimal.valueOf(3), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(2), BigDecimal.valueOf(4)
        );

        assertThat(result).isEqualByComparingTo("14");
    }

    @Test
    void supportsHeight() {
        BigDecimal result = RawMaterialConversionFormula.evaluate(
                "%c * %l * %a", BigDecimal.valueOf(3), BigDecimal.valueOf(2), BigDecimal.ZERO,
                BigDecimal.valueOf(4), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );

        assertThat(result).isEqualByComparingTo("24");
    }

    @Test
    void rejectsUnknownVariablesAndExecutableContent() {
        assertThatThrownBy(() -> RawMaterialConversionFormula.validate("%x * 2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("variável desconhecida");
        assertThatThrownBy(() -> RawMaterialConversionFormula.validate("alert(1)"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsDivisionByZeroWhenCalculatingAnItem() {
        assertThatThrownBy(() -> RawMaterialConversionFormula.evaluate(
                "%c / %l", BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("divisão por zero");
    }
}
