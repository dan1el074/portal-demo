package br.com.metaro.portal.modules.engineering.trelloIntegration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrelloIntegrationSettingsDto {
    private Integer retentionDays;
    private Integer erpLookbackDays;
    private String destinationEmail;
    private String ccEmail;
}
