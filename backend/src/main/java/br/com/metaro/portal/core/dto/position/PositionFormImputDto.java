package br.com.metaro.portal.core.dto.position;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PositionFormImputDto {
    @NotBlank(message = "Nome é obrigatório")
    String name;
    @NotEmpty(message = "Lista de gestores não pode ser vazia")
    List<Long> manangers;
    Boolean activated;
}
