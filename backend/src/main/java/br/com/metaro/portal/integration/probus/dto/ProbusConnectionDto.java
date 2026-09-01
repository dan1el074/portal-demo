package br.com.metaro.portal.integration.probus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProbusConnectionDto {
    private final String jdbcUrl;
    private final String username;
    private final String password;
}
