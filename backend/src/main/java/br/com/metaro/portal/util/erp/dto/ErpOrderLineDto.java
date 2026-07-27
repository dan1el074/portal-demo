package br.com.metaro.portal.util.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErpOrderLineDto {
    private final Integer number;
    private final String client;
    private final String item;
}
