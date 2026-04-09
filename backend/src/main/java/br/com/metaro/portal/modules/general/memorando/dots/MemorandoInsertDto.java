package br.com.metaro.portal.modules.general.memorando.dots;

import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemorandoInsertDto {
    @Min(value = 4293, message = "Pedido inválido")
    private Long request;
    @NotBlank(message = "Cliente é obrigatório")
    private String client;
    @NotEmpty(message = "Lista de itens não pode ser vazia")
    private List<String> items;
    @NotBlank(message = "Título é obrigatório")
    private String title;
    @NotBlank(message = "Descrição é obrigatório")
    private String description;
    @NotBlank(message = "Motivo é obrigatório")
    private String reason;
    private MemorandoStatus status;
    @NotEmpty(message = "Lista de departamentos não pode ser vazia")
    private List<Long> departments;
}
