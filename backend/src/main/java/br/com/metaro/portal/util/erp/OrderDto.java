package br.com.metaro.portal.util.erp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderDto {
    private final Integer number;
    private final String client;
    private final String item;
}
