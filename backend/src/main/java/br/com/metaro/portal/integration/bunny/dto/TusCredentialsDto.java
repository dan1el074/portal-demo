package br.com.metaro.portal.integration.bunny.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TusCredentialsDto {

    private final String signature;
    private final long expiration;
}
