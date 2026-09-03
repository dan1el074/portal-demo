package br.com.metaro.portal.integration.bunny.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BunnyConfigUpdateDto {
    @NotBlank(message = "Informe o Library ID do Bunny Stream.")
    private String libraryId;
    private String apiKey;
}
