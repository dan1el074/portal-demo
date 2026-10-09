package br.com.metaro.portal.modules.pcp.kanbamExclusion;

import br.com.metaro.portal.modules.pcp.kanbamExclusion.dto.KanbamItemDto;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/pcp/kanbam-exclusion")
public class KanbamExclusionController {
    private final KanbamExclusionClient client;

    public KanbamExclusionController(KanbamExclusionClient client) {
        this.client = client;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_KANBAM_EXCLUSION')")
    @GetMapping
    public ResponseEntity<List<KanbamItemDto>> findByLotNumber(
            @RequestParam @Positive Long lotNumber
    ) {
        return ResponseEntity.ok(client.findByLotNumber(lotNumber));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_KANBAM_EXCLUSION')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteManufacturingOrder(
            @PathVariable @Positive Long orderId
    ) {
        client.deleteManufacturingOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
