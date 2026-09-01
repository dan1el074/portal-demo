package br.com.metaro.portal.integration.bunny;

import br.com.metaro.portal.integration.bunny.dto.BunnyConfigDto;
import br.com.metaro.portal.integration.bunny.dto.BunnyConfigUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integrations/bunny/config")
@RequiredArgsConstructor
public class BunnyConfigController {
    private final BunnyConfigService configService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SYSTEM_PARAMS')")
    public ResponseEntity<BunnyConfigDto> getConfig() {
        return ResponseEntity.ok(configService.getConfig());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SYSTEM_PARAMS')")
    public ResponseEntity<BunnyConfigDto> updateConfig(@Valid @RequestBody BunnyConfigUpdateDto dto) {
        return ResponseEntity.ok(configService.updateConfig(dto));
    }
}
