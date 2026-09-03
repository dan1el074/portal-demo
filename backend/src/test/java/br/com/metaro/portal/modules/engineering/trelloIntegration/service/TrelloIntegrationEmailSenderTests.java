package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationRecord;
import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus;
import br.com.metaro.portal.modules.engineering.trelloIntegration.repository.TrelloIntegrationRepository;
import br.com.metaro.portal.util.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrelloIntegrationEmailSenderTests {
    private TrelloIntegrationRepository repository;
    private EmailService emailService;
    private TrelloIntegrationEmailSender sender;

    @BeforeEach
    void setUp() {
        repository = mock(TrelloIntegrationRepository.class);
        emailService = mock(EmailService.class);
        sender = new TrelloIntegrationEmailSender(repository, new TrelloPlainTextEmailFactory(), emailService);
    }

    @Test
    void sendsPendingRecordAndMarksItAsSent() {
        TrelloIntegrationRecord record = pendingRecord();
        when(repository.findById(1L)).thenReturn(Optional.of(record));

        sender.sendPending(1L);

        assertThat(record.getStatus()).isEqualTo(TrelloIntegrationStatus.SENT);
        assertThat(record.getSentAt()).isNotNull();
        verify(repository).saveAndFlush(record);
    }

    @Test
    void recordsFailureWithoutInterruptingTheScheduledBatch() throws Exception {
        TrelloIntegrationRecord record = pendingRecord();
        when(repository.findById(1L)).thenReturn(Optional.of(record));
        doThrow(new IllegalStateException("SMTP indisponível")).when(emailService)
                .sendPlainTextEmail(anyString(), any(), anyString(), anyString(), anyString());

        sender.sendPending(1L);

        assertThat(record.getStatus()).isEqualTo(TrelloIntegrationStatus.ERROR);
        assertThat(record.getErrorMessage()).isEqualTo("SMTP indisponível");
        verify(repository).saveAndFlush(record);
    }

    @Test
    void ignoresRecordThatIsNoLongerPending() throws Exception {
        TrelloIntegrationRecord record = pendingRecord();
        record.setStatus(TrelloIntegrationStatus.SENT);
        when(repository.findById(1L)).thenReturn(Optional.of(record));

        sender.sendPending(1L);

        verify(emailService, never()).sendPlainTextEmail(anyString(), any(), anyString(), anyString(), anyString());
        verify(repository, never()).saveAndFlush(record);
    }

    private TrelloIntegrationRecord pendingRecord() {
        TrelloIntegrationRecord record = new TrelloIntegrationRecord();
        record.setId(1L);
        record.setOrderNumber(14644L);
        record.setOrderType("Venda");
        record.setClient("Cliente");
        record.setProductCode("42971");
        record.setProductDescription("Produto");
        record.setQuantity(new BigDecimal("2"));
        record.setSeller("Vendedor");
        record.setReleaseAt(LocalDateTime.of(2026, 9, 1, 8, 26));
        record.setExpectedDelivery(LocalDate.of(2026, 9, 4));
        record.setDestinationEmail("trello@example.com");
        record.setStatus(TrelloIntegrationStatus.PENDING);
        return record;
    }
}
