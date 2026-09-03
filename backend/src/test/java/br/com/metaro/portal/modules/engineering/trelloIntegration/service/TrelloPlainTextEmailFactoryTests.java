package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TrelloPlainTextEmailFactoryTests {
    private final TrelloPlainTextEmailFactory factory = new TrelloPlainTextEmailFactory();

    @Test
    void createsTheExactPlainTextContractExpectedByTrello() {
        TrelloIntegrationRecord record = new TrelloIntegrationRecord();
        record.setOrderNumber(14644L);
        record.setOrderType("Venda");
        record.setClient("SBF COMERCIO PRODUTOS ESPORTIVOS");
        record.setProductCode("42971");
        record.setProductDescription("CARTUCHO MOLA GÁS METARO CAP. 6 MOLAS");
        record.setQuantity(new BigDecimal("2.000"));
        record.setSeller("Juan Rodrigues");
        record.setReleaseAt(LocalDateTime.of(2026, 9, 1, 8, 26));
        record.setExpectedDelivery(LocalDate.of(2026, 9, 4));

        assertThat(factory.subject(record)).isEqualTo("Pedido - 14644");
        assertThat(factory.body(record)).isEqualTo("""
                Tipo: Venda
                Cliente: SBF COMERCIO PRODUTOS ESPORTIVOS
                Código do produto: 42971
                Descrição do produto: CARTUCHO MOLA GÁS METARO CAP. 6 MOLAS
                Quantidade: 2
                Vendedor: Juan Rodrigues
                Liberação: 01/09/2026 às 08:26
                Previsão de entrega: 04/09/2026""");
    }
}
