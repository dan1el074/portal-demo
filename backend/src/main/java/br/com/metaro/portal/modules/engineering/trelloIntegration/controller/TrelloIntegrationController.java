package br.com.metaro.portal.modules.engineering.trelloIntegration.controller;

import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationConsultResultDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationRecordDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationSettingsDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationSettingsInputDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationSummaryDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.service.TrelloIntegrationService;
import br.com.metaro.portal.modules.engineering.trelloIntegration.service.TrelloIntegrationSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trello-integration")
@RequiredArgsConstructor
public class TrelloIntegrationController {
    private final TrelloIntegrationService service;
    private final TrelloIntegrationSettingsService settingsService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TRELLO_INTEGRATION','ROLE_TRELLO_INTEGRATION_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<TrelloIntegrationRecordDto>> list(
            Pageable pageable,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(service.list(pageable, search));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TRELLO_INTEGRATION','ROLE_TRELLO_INTEGRATION_ADMIN')")
    @GetMapping("/summary")
    public ResponseEntity<TrelloIntegrationSummaryDto> summary() {
        return ResponseEntity.ok(service.summary());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TRELLO_INTEGRATION_ADMIN')")
    @GetMapping("/settings")
    public ResponseEntity<TrelloIntegrationSettingsDto> settings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TRELLO_INTEGRATION_ADMIN')")
    @PutMapping("/settings")
    public ResponseEntity<TrelloIntegrationSettingsDto> updateSettings(
            @Valid @RequestBody TrelloIntegrationSettingsInputDto dto
    ) {
        return ResponseEntity.ok(settingsService.updateSettings(dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TRELLO_INTEGRATION','ROLE_TRELLO_INTEGRATION_ADMIN')")
    @PostMapping("/consult")
    public ResponseEntity<TrelloIntegrationConsultResultDto> consultErp() {
        return ResponseEntity.ok(service.consultErp());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TRELLO_INTEGRATION','ROLE_TRELLO_INTEGRATION_ADMIN')")
    @PostMapping("/{id}/resend")
    public ResponseEntity<TrelloIntegrationRecordDto> resend(@PathVariable Long id) {
        return ResponseEntity.ok(service.resend(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TRELLO_INTEGRATION','ROLE_TRELLO_INTEGRATION_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<TrelloIntegrationRecordDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
