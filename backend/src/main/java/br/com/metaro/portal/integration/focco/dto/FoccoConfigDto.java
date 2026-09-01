package br.com.metaro.portal.integration.focco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FoccoConfigDto {
    private final String baseUrl;
    private final String key;
    private final boolean tokenConfigured;
}
