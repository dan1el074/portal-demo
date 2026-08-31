package br.com.metaro.portal.integration.focco;

import br.com.metaro.portal.integration.focco.dto.FoccoConfigDto;
import br.com.metaro.portal.integration.focco.dto.FoccoConfigUpdateDto;
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
@RequestMapping("/api/integrations/focco/config")
@RequiredArgsConstructor
public class FoccoConfigController {
    private final FoccoConfigService configService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<FoccoConfigDto> getConfig() {
        return ResponseEntity.ok(configService.getConfig());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<FoccoConfigDto> updateConfig(@Valid @RequestBody FoccoConfigUpdateDto dto) {
        return ResponseEntity.ok(configService.updateConfig(dto));
    }
}
