package br.com.metaro.portal.modules.engineering.trelloIntegration.repository;

import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationRecord;
import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "external.datasource.jdbc-url=jdbc:h2:mem:trello-integration-repository-testdb",
        "external.datasource.driver-class-name=org.h2.Driver",
        "external.datasource.username=sa",
        "external.datasource.password="
})
class TrelloIntegrationRepositoryTests {
    @Autowired
    private TrelloIntegrationRepository repository;

    @Test
    void searchesThroughProjectionAndSummarizesStatuses() {
        repository.saveAll(List.of(
                record(14644L, "42971", "Cliente Alfa", TrelloIntegrationStatus.SENT),
                record(14644L, "42972", "Cliente Alfa", TrelloIntegrationStatus.SENT),
                record(14645L, "42971", "Cliente Beta", TrelloIntegrationStatus.PENDING),
                record(14646L, "42971", "Cliente Gama", TrelloIntegrationStatus.ERROR)
        ));
        repository.flush();

        var page = repository.search(PageRequest.of(0, 10), "Beta");
        var summary = repository.summarize();

        assertThat(page.getContent()).singleElement().satisfies(item ->
                assertThat(item.getOrderNumber()).isEqualTo(14645L));
        assertThat(summary.getTotal()).isEqualTo(4);
        assertThat(summary.getSent()).isEqualTo(2);
        assertThat(summary.getPending()).isEqualTo(1);
        assertThat(summary.getErrors()).isEqualTo(1);
    }

    private TrelloIntegrationRecord record(
            Long order,
            String productCode,
            String client,
            TrelloIntegrationStatus status
    ) {
        TrelloIntegrationRecord record = new TrelloIntegrationRecord();
        record.setOrderNumber(order);
        record.setOrderType("Venda");
        record.setClient(client);
        record.setProductCode(productCode);
        record.setProductDescription("Produto");
        record.setQuantity(new BigDecimal("2"));
        record.setSeller("Vendedor");
        record.setReleaseAt(LocalDateTime.of(2026, 9, 1, 8, 26));
        record.setExpectedDelivery(LocalDate.of(2026, 9, 4));
        record.setStatus(status);
        record.setImportedAt(Instant.now());
        record.setDestinationEmail("trello@example.com");
        return record;
    }
}
