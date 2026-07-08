package br.com.metaro.portal.modules.general.stepFlow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderItemInputDto {
    private Long id;
    private Integer producedQuantity;
    private Integer invoicedQuantity;
}
