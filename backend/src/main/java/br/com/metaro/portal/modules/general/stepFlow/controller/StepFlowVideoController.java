package br.com.metaro.portal.modules.general.stepFlow.controller;

import br.com.metaro.portal.modules.general.stepFlow.dto.StepFlowVideoCreateDto;
import br.com.metaro.portal.modules.general.stepFlow.service.StepFlowVideoService;
import br.com.metaro.portal.util.video.dto.VideoUploadDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/step-flow")
public class StepFlowVideoController {
    private final StepFlowVideoService stepFlowVideoService;

    public StepFlowVideoController(StepFlowVideoService stepFlowVideoService) {
        this.stepFlowVideoService = stepFlowVideoService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @PostMapping(value = "/{orderId}/video")
    public ResponseEntity<VideoUploadDto> createVideoUpload(
            @PathVariable Long orderId,
            @Valid @RequestBody StepFlowVideoCreateDto dto
    ) {
        return ResponseEntity.ok(stepFlowVideoService.createVideoUpload(orderId, dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @PutMapping(value = "/video/{id}/complete")
    public ResponseEntity<Void> completeVideoUpload(@PathVariable Long id) {
        stepFlowVideoService.completeVideoUpload(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STEP_FLOW')")
    @DeleteMapping(value = "/video/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        stepFlowVideoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}
