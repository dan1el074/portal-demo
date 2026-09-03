package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationRecord;
import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus;
import br.com.metaro.portal.modules.engineering.trelloIntegration.repository.TrelloIntegrationRepository;
import br.com.metaro.portal.util.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TrelloIntegrationEmailSender {
    private static final String EMAIL_MODULE = "Integração Trello";

    private final TrelloIntegrationRepository repository;
    private final TrelloPlainTextEmailFactory emailFactory;
    private final EmailService emailService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendPending(Long id) {
        TrelloIntegrationRecord record = repository.findById(id).orElse(null);
        if (record == null || record.getStatus() != TrelloIntegrationStatus.PENDING) return;
        send(record, false);
        repository.saveAndFlush(record);
    }

    @Transactional
    public void resend(TrelloIntegrationRecord record) {
        send(record, true);
        repository.saveAndFlush(record);
    }

    private boolean send(TrelloIntegrationRecord record, boolean resend) {
        try {
            emailService.sendPlainTextEmail(
                    record.getDestinationEmail(),
                    record.getCcEmail(),
                    emailFactory.subject(record),
                    emailFactory.body(record),
                    EMAIL_MODULE
            );
            Instant sentAt = Instant.now();
            record.setStatus(TrelloIntegrationStatus.SENT);
            record.setErrorMessage(null);
            if (record.getSentAt() == null) record.setSentAt(sentAt);
            if (resend) record.setLastResentAt(sentAt);
            return true;
        } catch (Exception exception) {
            record.setStatus(TrelloIntegrationStatus.ERROR);
            record.setErrorMessage(limitErrorMessage(exception.getMessage()));
            return false;
        }
    }

    private String limitErrorMessage(String message) {
        String safeMessage = StringUtils.hasText(message) ? message : "Falha não detalhada no envio do e-mail.";
        return safeMessage.length() <= 2000 ? safeMessage : safeMessage.substring(0, 2000);
    }
}
