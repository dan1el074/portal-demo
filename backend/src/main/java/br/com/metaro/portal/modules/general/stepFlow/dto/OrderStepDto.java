package br.com.metaro.portal.modules.general.stepFlow.dto;

import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStep;
import br.com.metaro.portal.modules.general.stepFlow.entities.StepMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderStepDto {
    private Long id;
    private String step;
    private String status;
    private String userName;
    private Instant startedAt;
    private Instant finishedAt;
    private List<StepMessageDto> messages;

    public OrderStepDto(OrderStep entity) {
        id = entity.getId();
        step = entity.getStep().toString();
        status = entity.getStatus().name();
        startedAt = entity.getStartedAt();
        finishedAt = entity.getFinishedAt();
        messages = new ArrayList<>();

        if (entity.getFinishedBy() != null) userName = entity.getFinishedBy().getName();

        for (StepMessage message : entity.getMessages()) {
            messages.add(new StepMessageDto(message));
        }
    }
}
