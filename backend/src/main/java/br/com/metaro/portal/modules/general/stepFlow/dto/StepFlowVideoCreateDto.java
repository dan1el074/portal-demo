package br.com.metaro.portal.modules.general.stepFlow.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StepFlowVideoCreateDto {
    @NotBlank
    @Size(max = 255)
    private String name;
}
