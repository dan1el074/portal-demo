package br.com.metaro.portal.modules.engineering.trelloIntegration.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FoccoTrelloResponseDto {
    private List<FoccoTrelloRecordDto> value;
    private boolean succeeded;
    private boolean failed;
    private boolean allFailed;
    private String baseErrorMessage;
    private String errorMessage;
}
