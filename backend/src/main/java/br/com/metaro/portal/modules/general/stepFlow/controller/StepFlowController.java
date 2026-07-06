package br.com.metaro.portal.modules.general.stepFlow.controller;

import br.com.metaro.portal.modules.general.stepFlow.dto.OrderDto;
import br.com.metaro.portal.modules.general.stepFlow.dto.OrderInputDto;
import br.com.metaro.portal.modules.general.stepFlow.dto.OrderMinDto;
import br.com.metaro.portal.modules.general.stepFlow.dto.StepFlowInfoDto;
import br.com.metaro.portal.modules.general.stepFlow.service.StepFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RestController
@RequestMapping(value = "/api/step-flow")
public class StepFlowController {
    @Autowired
    private StepFlowService stepFlowService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @GetMapping
    public ResponseEntity<Page<OrderMinDto>> listOrders(
            @RequestParam(required = false) String search, Pageable pageable) {
        Page<OrderMinDto> dtos = stepFlowService.listOrders(search, pageable);
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<OrderDto> findById(@PathVariable Long id) {
        OrderDto dto = stepFlowService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @GetMapping(value = "/step/{ordinal}")
    public ResponseEntity<List<OrderMinDto>> listOrdersByStep(@PathVariable Integer ordinal) {
        List<OrderMinDto> dtos = stepFlowService.findByCurrentStep(ordinal);
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW_ADMIN')")
    @GetMapping(value = "/admin")
    public ResponseEntity<StepFlowInfoDto> getStepFlowInfo() {
        StepFlowInfoDto dtos = stepFlowService.generateDashboard();
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @PostMapping(value = "/{orderNumber}")
    public ResponseEntity<Void> create (@PathVariable Integer orderNumber) {
        stepFlowService.create(orderNumber);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrderDto> update(@PathVariable Long id, @ModelAttribute OrderInputDto dto) throws IOException {
        OrderDto newDto = stepFlowService.update(id, dto);
        return ResponseEntity.ok(newDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @PutMapping(value = "/{id}/nextStep")
    public ResponseEntity<Void> nextStep(@PathVariable Long id) {
        stepFlowService.goToNextStep(id);
        return ResponseEntity.noContent().build();
    }
}
