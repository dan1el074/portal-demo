package br.com.metaro.portal.integration.probus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProbusConfigUpdateDto {
    @NotBlank(message = "Informe a URL JDBC do Probus.")
    private String jdbcUrl;
    @NotBlank(message = "Informe o usuário do Probus.")
    private String username;
    private String password;
}
