package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import br.com.metaro.portal.core.entities.Param;
import br.com.metaro.portal.core.repositories.ParamRepository;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationSettingsDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationSettingsInputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TrelloIntegrationSettingsService {
    static final String RETENTION_DAYS_PARAM = "trello.integration.retention-days";
    static final String ERP_LOOKBACK_DAYS_PARAM = "trello.integration.erp-lookback-days";
    static final String DESTINATION_EMAIL_PARAM = "trello.integration.destination-email";
    static final String CC_EMAIL_PARAM = "trello.integration.cc-email";

    private final ParamRepository paramRepository;

    @Transactional(readOnly = true)
    public TrelloIntegrationSettingsDto getSettings() {
        return new TrelloIntegrationSettingsDto(
                parsePositiveInteger(RETENTION_DAYS_PARAM, 90),
                parsePositiveInteger(ERP_LOOKBACK_DAYS_PARAM, 7),
                findValue(DESTINATION_EMAIL_PARAM).trim(),
                findValue(CC_EMAIL_PARAM).trim()
        );
    }

    @Transactional
    public TrelloIntegrationSettingsDto updateSettings(TrelloIntegrationSettingsInputDto dto) {
        String destinationEmail = dto.getDestinationEmail().trim();
        String ccEmail = StringUtils.hasText(dto.getCcEmail()) ? dto.getCcEmail().trim() : "";

        saveValue(RETENTION_DAYS_PARAM, dto.getRetentionDays().toString());
        saveValue(ERP_LOOKBACK_DAYS_PARAM, dto.getErpLookbackDays().toString());
        saveValue(DESTINATION_EMAIL_PARAM, destinationEmail);
        saveValue(CC_EMAIL_PARAM, ccEmail);

        return new TrelloIntegrationSettingsDto(
                dto.getRetentionDays(), dto.getErpLookbackDays(), destinationEmail, ccEmail
        );
    }

    public void validateReadyForConsult(TrelloIntegrationSettingsDto settings) {
        if (!StringUtils.hasText(settings.getDestinationEmail())) {
            throw new UnprocessableEntityException("Configure a caixa de e-mail destinatária do Trello antes da consulta.");
        }
    }

    private int parsePositiveInteger(String name, int defaultValue) {
        String value = findValue(name);
        if (!StringUtils.hasText(value)) return defaultValue;
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    private String findValue(String name) {
        return paramRepository.findByName(name).map(Param::getContent).orElse("");
    }

    private void saveValue(String name, String content) {
        Param param = paramRepository.findByName(name).orElseGet(() -> new Param(null, name, null));
        param.setContent(content);
        paramRepository.save(param);
    }
}
