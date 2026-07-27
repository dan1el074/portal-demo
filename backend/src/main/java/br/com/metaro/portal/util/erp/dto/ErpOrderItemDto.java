package br.com.metaro.portal.util.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErpOrderItemDto {
    private final String code;
    private final String description;
    private final Double unitValue;
    private final String unit;
    private final Integer quantity;
    private Integer producedQuantity;

    public void addProducedQuantity(Integer quantity) {
        producedQuantity += quantity;
    }
}
