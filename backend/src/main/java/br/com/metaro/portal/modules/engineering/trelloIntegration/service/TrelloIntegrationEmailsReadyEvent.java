package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TrelloIntegrationEmailsReadyEvent {
    private List<Long> recordIds;
}
