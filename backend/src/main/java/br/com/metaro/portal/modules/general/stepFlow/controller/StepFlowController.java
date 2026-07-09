package br.com.metaro.portal.modules.general.stepFlow.controller;

import br.com.metaro.portal.modules.general.stepFlow.dto.*;
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
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String stepFilter
    ) {
        String step = (stepFilter != null && !stepFilter.isBlank()) ? stepFilter : null;
        Page<OrderMinDto> dtos = stepFlowService.listOrders(pageable, search, step);
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
        List<OrderMinDto> dtos = stepFlowService.listOrdersByCurrentStep(ordinal);
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW_ADMIN')")
    @GetMapping(value = "/admin")
    public ResponseEntity<StepFlowInfoDto> getStepFlowInfo() {
        StepFlowInfoDto dtos = stepFlowService.generateDashboard();
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @PostMapping
    public ResponseEntity<Void> create (@RequestBody ErpOrderDto erpOrderDto) {
        stepFlowService.create(erpOrderDto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDto> update(
            @PathVariable Long id,
            @ModelAttribute OrderInputDto dto,
            @RequestParam(value = "itemsJson", required = false) String itemsJson,
            @RequestParam(value = "setStage", required = false) String stepId
    ) throws IOException {
        if (stepId != null) dto.setNewStepId(Integer.parseInt(stepId));
        dto.setItems(stepFlowService.parseItems(itemsJson));
        OrderDto newDto = stepFlowService.update(id, dto);
        return ResponseEntity.ok(newDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @PutMapping(value = "/{id}/nextStep")
    public ResponseEntity<Void> nextStep(@PathVariable Long id) {
        stepFlowService.goToNextStep(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @DeleteMapping(value = "/image/{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable Long id) throws IOException {
        stepFlowService.deleteImageById(id);
        return ResponseEntity.noContent().build();
    }
}
