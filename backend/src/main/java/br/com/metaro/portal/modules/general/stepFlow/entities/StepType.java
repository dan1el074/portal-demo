package br.com.metaro.portal.modules.general.stepFlow.entities;

import br.com.metaro.portal.util.others.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum StepType {
    FINAL_ASSEMBLY("Montagem Final"),
    PCP("PCP"),
    FREIGHT("Frete"),
    BILLING("Faturamento"),
    SHIPPING("Expedição");

    private final String description;

    public static StepType fromDescription(String text) {
        return Arrays.stream(values())
                .filter(e -> StringUtils.normalize(e.getDescription())
                        .equals(StringUtils.normalize(text)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return description;
    }
}
