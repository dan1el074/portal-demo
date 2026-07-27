package br.com.metaro.portal.util.video.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VideoUploadDto {
    private Long id;
    @JsonProperty("bunnyVideoId")
    private String providerVideoId;
    private String libraryId;
    private String uploadEndpoint;
    private String authorizationSignature;
    private Long authorizationExpire;
    @JsonProperty("viewUrl")
    private String playbackUrl;
}
