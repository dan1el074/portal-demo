package br.com.metaro.portal.util.erp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExternalCustomerDto {
    private final Long id;
    private final String name;
    private final String status;
}
