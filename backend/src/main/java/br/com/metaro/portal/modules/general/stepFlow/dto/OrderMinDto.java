package br.com.metaro.portal.modules.general.stepFlow.dto;

import br.com.metaro.portal.modules.general.stepFlow.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderMinDto {
    private Long id;
    private Integer number;
    private String client;
    private String startDate;
    private String dueDate;
    private String currentStep;
    private String status;
    private List<Integer> progress;

    public OrderMinDto(Order entity) {
        id = entity.getId();
        number = entity.getNumber();
        client = entity.getClient();
        startDate = entity.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        dueDate = entity.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        currentStep = entity.getCurrentStep().toString();
        status = entity.getStatus().toString();
        progress = entity.getProgress();
    }
}
