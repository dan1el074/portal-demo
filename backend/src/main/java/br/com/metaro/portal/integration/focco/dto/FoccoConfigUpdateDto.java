package br.com.metaro.portal.integration.focco.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FoccoConfigUpdateDto {
    @NotBlank(message = "Informe a Chave do FoccoERP.")
    private String key;
    private String token;
}
