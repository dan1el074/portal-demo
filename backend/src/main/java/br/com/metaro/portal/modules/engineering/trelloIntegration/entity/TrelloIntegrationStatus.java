package br.com.metaro.portal.modules.engineering.trelloIntegration.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrelloIntegrationStatus {
    SENT("Enviado"),
    PENDING("Pendente"),
    ERROR("Erro no envio");

    private final String description;
}
