package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrelloIntegrationEmailScheduler {
    private final TaskScheduler taskScheduler;
    private final TrelloIntegrationEmailSender emailSender;
    private final ApplicationEventPublisher eventPublisher;

    public void schedule(List<Long> recordIds) {
        if (recordIds.isEmpty()) return;
        eventPublisher.publishEvent(new TrelloIntegrationEmailsReadyEvent(List.copyOf(recordIds)));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void scheduleAfterCommit(TrelloIntegrationEmailsReadyEvent event) {
        taskScheduler.schedule(
                () -> event.getRecordIds().forEach(this::sendSafely),
                Instant.now().plusSeconds(1)
        );
    }

    private void sendSafely(Long recordId) {
        try {
            emailSender.sendPending(recordId);
        } catch (Exception exception) {
            log.error("Falha inesperada ao processar o registro {} da Integração Trello.", recordId, exception);
        }
    }
}
