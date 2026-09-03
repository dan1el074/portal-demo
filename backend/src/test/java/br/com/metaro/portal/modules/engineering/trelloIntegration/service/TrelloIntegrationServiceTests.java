package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationSettingsDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationRecord;
import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus;
import br.com.metaro.portal.modules.engineering.trelloIntegration.integration.FoccoTrelloClient;
import br.com.metaro.portal.modules.engineering.trelloIntegration.integration.dto.FoccoTrelloRecordDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.repository.TrelloIntegrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrelloIntegrationServiceTests {
    private TrelloIntegrationRepository repository;
    private TrelloIntegrationSettingsService settingsService;
    private FoccoTrelloClient foccoClient;
    private TrelloIntegrationEmailScheduler emailScheduler;
    private TrelloIntegrationEmailSender emailSender;
    private TrelloIntegrationService service;

    @BeforeEach
    void setUp() {
        repository = mock(TrelloIntegrationRepository.class);
        settingsService = mock(TrelloIntegrationSettingsService.class);
        foccoClient = mock(FoccoTrelloClient.class);
        emailScheduler = mock(TrelloIntegrationEmailScheduler.class);
        emailSender = mock(TrelloIntegrationEmailSender.class);
        service = new TrelloIntegrationService(
                repository,
                settingsService,
                foccoClient,
                new FoccoTrelloRecordMapper(),
                emailScheduler,
                emailSender
        );
    }

    @Test
    void persistsNewRecordsAsPendingAndSchedulesEmails() {
        TrelloIntegrationSettingsDto settings = new TrelloIntegrationSettingsDto(
                90, 7, "trello@example.com", "copy@example.com"
        );
        List<TrelloIntegrationRecord> saved = new ArrayList<>();
        when(settingsService.getSettings()).thenReturn(settings);
        when(repository.deleteImportedBefore(any(Instant.class))).thenReturn(2);
        when(foccoClient.findRecordsFrom(any(LocalDate.class))).thenReturn(List.of(
                erpRecord(14644L, "42971"),
                erpRecord(14644L, "42972"),
                erpRecord(14645L, "42971"),
                erpRecord(14644L, "42971")
        ));
        when(repository.existsByOrderNumberAndProductCode(any(Long.class), anyString())).thenReturn(false);
        when(repository.saveAllAndFlush(any())).thenAnswer(invocation -> {
            List<TrelloIntegrationRecord> records = invocation.getArgument(0);
            for (int index = 0; index < records.size(); index++) records.get(index).setId((long) index + 1);
            saved.addAll(records);
            return records;
        });

        var result = service.consultErp();

        assertThat(result.getReceived()).isEqualTo(4);
        assertThat(result.getImported()).isEqualTo(3);
        assertThat(result.getIgnored()).isEqualTo(1);
        assertThat(result.getScheduled()).isEqualTo(3);
        assertThat(result.getRemovedByRetention()).isEqualTo(2);
        assertThat(saved).extracting(TrelloIntegrationRecord::getStatus)
                .containsExactly(
                        TrelloIntegrationStatus.PENDING,
                        TrelloIntegrationStatus.PENDING,
                        TrelloIntegrationStatus.PENDING
                );
        verify(emailScheduler).schedule(List.of(1L, 2L, 3L));
        verify(foccoClient).findRecordsFrom(LocalDate.now().minusDays(7));
    }

    private FoccoTrelloRecordDto erpRecord(Long orderNumber, String productCode) {
        return new FoccoTrelloRecordDto(
                orderNumber,
                "Venda",
                "Cliente",
                productCode,
                "Produto",
                new BigDecimal("2"),
                "Vendedor",
                "01/09/2026 08:26",
                "04/09/2026"
        );
    }
}
