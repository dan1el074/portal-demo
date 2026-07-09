package br.com.metaro.portal.modules.general.stepFlow.controller;

import br.com.metaro.portal.modules.general.stepFlow.dto.ErpOrderDto;
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
    public ResponseEntity<ErpOrderDto> findOrderByNumber(@PathVariable Integer orderNumber) {
        ErpOrderDto dto = erpOrderService.findOrderByNumber(orderNumber);
        return ResponseEntity.ok(dto);
    }
}
