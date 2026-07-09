package br.com.metaro.portal.modules.general.stepFlow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StepFlowInfoDto {
    private Integer totalCount;
    private Integer completeCount;
    private Integer progressCount;
    private Integer lateCount;
    private List<Integer> stepsCount;
}
