package br.com.metaro.portal.integration.probus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProbusConfigDto {
    private final String jdbcUrl;
    private final String username;
    private final boolean passwordConfigured;
}
