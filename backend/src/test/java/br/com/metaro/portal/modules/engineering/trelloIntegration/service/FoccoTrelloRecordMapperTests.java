package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus;
import br.com.metaro.portal.modules.engineering.trelloIntegration.integration.dto.FoccoTrelloRecordDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FoccoTrelloRecordMapperTests {
    private final FoccoTrelloRecordMapper mapper = new FoccoTrelloRecordMapper();

    @Test
    void mapsTheDateFormatsUsedByTheFoccoExport() {
        FoccoTrelloRecordDto source = new FoccoTrelloRecordDto(
                14644L,
                "Venda",
                "Cliente",
                "42971",
                "Produto",
                new BigDecimal("2"),
                "Vendedor",
                "01/09/2026 08:26",
                "04/09/2026"
        );

        var record = mapper.map(source, "trello@example.com", "copy@example.com");

        assertThat(record.getReleaseAt()).isEqualTo(LocalDateTime.of(2026, 9, 1, 8, 26));
        assertThat(record.getExpectedDelivery()).isEqualTo(LocalDate.of(2026, 9, 4));
        assertThat(record.getStatus()).isEqualTo(TrelloIntegrationStatus.PENDING);
        assertThat(record.getDestinationEmail()).isEqualTo("trello@example.com");
        assertThat(record.getCcEmail()).isEqualTo("copy@example.com");
    }
}
