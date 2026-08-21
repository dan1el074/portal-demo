package br.com.metaro.portal.util.email;

import lombok.Getter;

import java.time.Instant;

@Getter
public class EmailLogDto {
    private final Long id;
    private final String subject;
    private final String recipient;
    private final String module;
    private final EmailStatus status;
    private final String errorMessage;
    private final Instant createdAt;

    public EmailLogDto(EmailLog entity) {
        id = entity.getId();
        subject = entity.getSubject();
        recipient = entity.getRecipient();
        module = entity.getModule();
        status = entity.getStatus();
        errorMessage = entity.getErrorMessage();
        createdAt = entity.getCreatedAt();
    }
}
