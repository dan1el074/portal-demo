package br.com.metaro.portal.modules.engineering.trelloIntegration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrelloIntegrationConsultResultDto {
    private Integer received;
    private Integer imported;
    private Integer ignored;
    private Integer scheduled;
    private Integer removedByRetention;
}
