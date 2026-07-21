package br.com.metaro.portal.modules.general.stepFlow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StepFlowVideoUploadDto {
    private Long id;
    private String bunnyVideoId;
    private String libraryId;
    private String uploadEndpoint;
    private String authorizationSignature;
    private Long authorizationExpire;
    private String viewUrl;
}
