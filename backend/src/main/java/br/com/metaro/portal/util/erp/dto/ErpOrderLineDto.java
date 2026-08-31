package br.com.metaro.portal.util.erp.dto;

import br.com.metaro.portal.util.erp.ErpSource;
import lombok.Getter;

@Getter
public class ErpOrderLineDto {
    private final Integer number;
    private final String client;
    private final String item;
    private final ErpSource source;

    public ErpOrderLineDto(Integer number, String client, String item) {
        this(number, client, item, ErpSource.PROBUS);
    }

    public ErpOrderLineDto(Integer number, String client, String item, ErpSource source) {
        this.number = number;
        this.client = client;
        this.item = item;
        this.source = source;
    }
}
