package br.com.metaro.portal.util.erp;

import br.com.metaro.portal.util.erp.dto.ErpOrderLineDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/erp/order")
public class ErpOrderController {
    private final ErpOrderQueryService erpOrderQueryService;

    public ErpOrderController(ErpOrderQueryService erpOrderQueryService) {
        this.erpOrderQueryService = erpOrderQueryService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{number}")
    public ResponseEntity<List<ErpOrderLineDto>> findOrderLinesByNumber(
            @PathVariable Integer number
    ) {
        return ResponseEntity.ok(
                erpOrderQueryService.findOrderLinesByNumber(number)
        );
    }
}
