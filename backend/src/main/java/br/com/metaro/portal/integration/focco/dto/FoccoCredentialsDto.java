package br.com.metaro.portal.integration.focco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FoccoCredentialsDto {
    private final String baseUrl;
    private final String key;
    private final String token;
}
