package br.com.metaro.portal.modules.general.stepFlow.dto;

import br.com.metaro.portal.modules.general.stepFlow.entities.StepFlowVideo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StepFlowVideoDto {
    private Long id;
    private String name;
    private String viewUrl;
    private String status;
    private Instant createdAt;
    private Boolean isCurrentStep;

    public StepFlowVideoDto(StepFlowVideo entity, Boolean isCurrentStep) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.viewUrl = entity.getViewUrl();
        this.status = entity.getStatus().name();
        this.createdAt = entity.getCreatedAt();
        this.isCurrentStep = isCurrentStep;
    }
}
