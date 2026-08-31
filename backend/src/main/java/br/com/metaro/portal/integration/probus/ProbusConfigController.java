package br.com.metaro.portal.integration.probus;

import br.com.metaro.portal.integration.probus.dto.ProbusConfigDto;
import br.com.metaro.portal.integration.probus.dto.ProbusConfigUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integrations/probus/config")
@RequiredArgsConstructor
public class ProbusConfigController {
    private final ProbusConfigService configService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<ProbusConfigDto> getConfig() {
        return ResponseEntity.ok(configService.getConfig());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<ProbusConfigDto> updateConfig(@Valid @RequestBody ProbusConfigUpdateDto dto) {
        return ResponseEntity.ok(configService.updateConfig(dto));
    }
}
