package br.com.metaro.portal.modules.general.stepFlow.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ErpOrderDto {
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
    private final List<ErpOrderItemDto> items;

    public ErpOrderDto(Integer number, String client, String cnpj, String phone, String salesperson,
                       LocalDate startDate, LocalDate dueDate, String address, Double subtotal, Double discount,
                       Double total) {
        this.number = number;
        this.client = client;
        this.cnpj = cnpj;
        this.phone = phone;
        this.salesperson = salesperson;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.address = address;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.items = new ArrayList<>();
    }
}
