package br.com.metaro.portal.modules.general.stepFlow.controller;

import br.com.metaro.portal.modules.general.stepFlow.dto.StepFlowVideoCreateDto;
import br.com.metaro.portal.modules.general.stepFlow.dto.StepFlowVideoUploadDto;
import br.com.metaro.portal.modules.general.stepFlow.service.StepFlowVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/step-flow")
public class StepFlowVideoController {
    @Autowired
    private StepFlowVideoService stepFlowVideoService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @PostMapping(value = "/{orderId}/video")
    public ResponseEntity<StepFlowVideoUploadDto> create(
            @PathVariable Long orderId,
            @RequestBody StepFlowVideoCreateDto dto
    ) {
        return ResponseEntity.ok(stepFlowVideoService.create(orderId, dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @PutMapping(value = "/video/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long id) {
        stepFlowVideoService.complete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @DeleteMapping(value = "/video/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        stepFlowVideoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
