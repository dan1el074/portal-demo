package br.com.metaro.portal.modules.engineering.trelloIntegration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrelloIntegrationSettingsInputDto {
    @NotNull(message = "Informe o número de dias de retenção.")
    @Min(value = 1, message = "A retenção deve ser de pelo menos 1 dia.")
    @Max(value = 3650, message = "A retenção deve ser de no máximo 3650 dias.")
    private Integer retentionDays;

    @NotNull(message = "Informe o número de dias da consulta ao ERP.")
    @Min(value = 1, message = "A consulta deve considerar pelo menos 1 dia.")
    @Max(value = 3650, message = "A consulta deve considerar no máximo 3650 dias.")
    private Integer erpLookbackDays;

    @NotBlank(message = "Informe a caixa de e-mail destinatária do Trello.")
    @Email(message = "Informe uma caixa de e-mail destinatária válida.")
    private String destinationEmail;

    @Email(message = "Informe um e-mail em cópia válido.")
    private String ccEmail;
}
