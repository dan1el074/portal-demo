package br.com.metaro.portal.integration.focco.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FoccoOrderLineDto {
    @JsonProperty("order_number")
    private Integer orderNumber;
    private String client;
    private String cnpj;
    private String phone;
    private String seller;
    @JsonProperty("start_date_data")
    private LocalDateTime startDate;
    @JsonProperty("due_date_data")
    private LocalDateTime dueDate;
    @JsonProperty("adress")
    private String address;
    private Double subtotal;
    private Double discount;
    private Double total;
    @JsonProperty("item_code")
    private String itemCode;
    @JsonProperty("item_description")
    private String itemDescription;
    @JsonProperty("item_unit_value")
    private Double itemUnitValue;
    @JsonProperty("item_unit")
    private String itemUnit;
    @JsonProperty("item_quantity")
    private BigDecimal itemQuantity;
}
