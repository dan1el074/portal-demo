package br.com.metaro.portal.util.email;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email-logs")
@RequiredArgsConstructor
public class EmailLogController {
    private final EmailLogService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SYSTEM_PARAMS')")
    public ResponseEntity<Page<EmailLogDto>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }
}
