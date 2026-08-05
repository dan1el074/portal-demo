package br.com.metaro.portal.modules.general.stepFlow.controller;

import br.com.metaro.portal.modules.general.stepFlow.service.StepFlowErpOrderService;
import br.com.metaro.portal.util.erp.dto.ErpOrderDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/step-flow/erp")
public class ErpStepFlowController {
    private final StepFlowErpOrderService stepFlowErpOrderService;

    public ErpStepFlowController(StepFlowErpOrderService stepFlowErpOrderService) {
        this.stepFlowErpOrderService = stepFlowErpOrderService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @GetMapping(value = "/{orderNumber}")
    public ResponseEntity<ErpOrderDto> findAvailableOrderByNumber(
            @PathVariable Integer orderNumber
    ) {
        ErpOrderDto dto = stepFlowErpOrderService
                .findAvailableOrderByNumber(orderNumber);
        return ResponseEntity.ok(dto);
    }
}
