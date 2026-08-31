package br.com.metaro.portal.integration.focco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FoccoOrderResponseDto {
    private List<FoccoOrderLineDto> value;
    private boolean succeeded;
    private boolean failed;
    private boolean allFailed;
    private String baseErrorMessage;
    private String errorMessage;
}
