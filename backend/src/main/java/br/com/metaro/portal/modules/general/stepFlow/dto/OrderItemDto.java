package br.com.metaro.portal.modules.general.stepFlow.dto;

import br.com.metaro.portal.modules.general.stepFlow.entities.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderItemDto {
    private Long id;
    private Integer code;
    private String description;
    private String unit;
    private Double unitPrice;
    private Integer quantity;
    private Integer producedQuantity;
    private Integer invoicedQuantity;
    private Double total;

    public OrderItemDto(OrderItem entity) {
        id = entity.getId();
        code = entity.getItemCode();
        description = entity.getDescription();
        unit = entity.getUnit();
        unitPrice = entity.getUnitPrice();
        quantity = entity.getQuantity();
        producedQuantity = entity.getProducedQuantity();
        invoicedQuantity = entity.getInvoicedQuantity();
        total = entity.getTotal();
    }
}
