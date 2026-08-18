package br.com.metaro.portal.modules.general.rawMaterials.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RawMaterialDtoValidationTests {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void rejectsBlankIdentificationAndMissingCategory() {
        RawMaterialInputDto dto = validItem();
        dto.setCode(" ");
        dto.setName("");
        dto.setCategoryId(null);

        assertThat(validator.validate(dto))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("code", "name", "categoryId");
    }

    @Test
    void rejectsNegativeStockAndDimensions() {
        RawMaterialInputDto dto = validItem();
        dto.setCurrentStorage(new BigDecimal("-1"));
        dto.setLength(new BigDecimal("-0.001"));

        assertThat(validator.validate(dto))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("currentStorage", "length");
    }

    @Test
    void acceptsStringCodeZeroStocksAndMissingOptionalDimensions() {
        RawMaterialInputDto dto = validItem();
        dto.setCode("ACO-10/20");
        dto.setCurrentStorage(BigDecimal.ZERO);
        dto.setMinStorage(BigDecimal.ZERO);
        dto.setMaxStorage(BigDecimal.ZERO);
        dto.setDescription(null);
        dto.setLength(null);
        dto.setWidth(null);
        dto.setThickness(null);
        dto.setWeightPerSquareMeter(null);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void rejectsDescriptionWithLessThanFiveCharacters() {
        RawMaterialInputDto dto = validItem();
        dto.setName("Aço");

        assertThat(validator.validate(dto))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("name");
    }

    @Test
    void validatesStockMovementAndRetentionLimits() {
        assertThat(validator.validate(new RawMaterialStockDto(new BigDecimal("-1")))).isNotEmpty();
        assertThat(validator.validate(new RawMaterialHistoryRetentionDto(9))).isNotEmpty();
        assertThat(validator.validate(new RawMaterialHistoryRetentionDto(100001))).isNotEmpty();
        assertThat(validator.validate(new RawMaterialHistoryRetentionDto(1000))).isEmpty();
    }

    static RawMaterialInputDto validItem() {
        return new RawMaterialInputDto("001", "Chapa", "", BigDecimal.TEN, BigDecimal.ONE,
                BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                1L, true);
    }
}
