package br.com.metaro.portal.integration.bunny.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BunnyConfigDto {
    private final String libraryId;
    private final boolean apiKeyConfigured;
}
