package br.com.metaro.portal.modules.general.stepFlow.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bunny.stream")
@Getter
@Setter
public class BunnyStreamProperties {
    private String apiKey;
    private String libraryId;
    private String embedBaseUrl;
}
