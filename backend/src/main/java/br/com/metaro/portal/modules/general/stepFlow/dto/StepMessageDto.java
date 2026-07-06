package br.com.metaro.portal.modules.general.stepFlow.dto;

import br.com.metaro.portal.modules.general.stepFlow.entities.StepMessage;
import br.com.metaro.portal.util.others.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StepMessageDto {
    private Long id;
    private Instant created;
    private String userName;
    private String message;

    public StepMessageDto(StepMessage entity) {
        id = entity.getId();
        created = entity.getCreatedAt();
        message = StringUtils.toHtml(entity.getMessage());

        if (entity.getCreatedBy() != null) userName = entity.getCreatedBy().getName();
    }
}
