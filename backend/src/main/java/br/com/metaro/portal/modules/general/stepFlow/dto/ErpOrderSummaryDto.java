package br.com.metaro.portal.modules.general.stepFlow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class ErpOrderSummaryDto {
    private final Integer number;
    private final String client;
    private final String cnpj;
    private final String phone;
    private final String salesperson;
    private final LocalDate startDate;
    private final LocalDate dueDate;
    private final String address;
    private final Double subtotal;
    private final Double discount;
    private final Double total;
}
