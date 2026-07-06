package br.com.metaro.portal.modules.general.stepFlow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErpOrderItemDto {
    private final Integer code;
    private final String description;
    private final Double unitValue;
    private final String unit;
    private final Integer quantity;
}
