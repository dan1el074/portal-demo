package br.com.metaro.portal.modules.general.stepFlow.controller;

import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.modules.general.stepFlow.dto.ErpOrderSummaryDto;
import br.com.metaro.portal.modules.general.stepFlow.service.ErpOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/step-flow/erp")
public class ErpStepFlowController {
    @Autowired
    private ErpOrderService erpOrderService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @GetMapping(value = "/{orderNumber}")
    public ResponseEntity<ErpOrderSummaryDto> findByOrder(@PathVariable Integer orderNumber) {
        ErpOrderSummaryDto dto = erpOrderService.findByOrder(orderNumber)
                .orElseThrow(ResourceNotFoundException::new);
        return ResponseEntity.ok(dto);
    }
}
