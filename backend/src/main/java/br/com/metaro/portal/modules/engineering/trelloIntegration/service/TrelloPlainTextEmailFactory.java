package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationRecord;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class TrelloPlainTextEmailFactory {
    private static final DateTimeFormatter RELEASE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    private static final DateTimeFormatter DELIVERY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String subject(TrelloIntegrationRecord record) {
        return "Pedido - " + record.getOrderNumber();
    }

    public String body(TrelloIntegrationRecord record) {
        return String.join("\n",
                "Tipo: " + record.getOrderType(),
                "Cliente: " + record.getClient(),
                "Código do produto: " + record.getProductCode(),
                "Descrição do produto: " + record.getProductDescription(),
                "Quantidade: " + record.getQuantity().stripTrailingZeros().toPlainString(),
                "Vendedor: " + record.getSeller(),
                "Liberação: " + RELEASE_FORMAT.format(record.getReleaseAt()),
                "Previsão de entrega: " + DELIVERY_FORMAT.format(record.getExpectedDelivery())
        );
    }
}
