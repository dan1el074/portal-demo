package br.com.metaro.portal.util.email;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailLogService {
    private final EmailLogRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String recipient, String subject, String module, EmailStatus status, Exception error) {
        String errorMessage = error == null ? null : error.getMessage();
        if (errorMessage != null && errorMessage.length() > 2000) errorMessage = errorMessage.substring(0, 2000);
        repository.save(new EmailLog(null, limit(subject, 255), limit(recipient, 320), limit(module, 100),
                status, errorMessage, Instant.now()));
    }

    @Transactional(readOnly = true)
    public Page<EmailLogDto> list(Pageable pageable) {
        int size = Math.clamp(pageable.getPageSize(), 1, 100);
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), size,
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));
        return repository.findAll(sorted).map(EmailLogDto::new);
    }

    private String limit(String value, int maxLength) {
        String safeValue = value == null ? "" : value;
        return safeValue.length() <= maxLength ? safeValue : safeValue.substring(0, maxLength);
    }
}
