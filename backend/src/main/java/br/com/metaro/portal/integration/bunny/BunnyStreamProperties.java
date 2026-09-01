package br.com.metaro.portal.integration.bunny;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bunny.stream")
@Getter
@Setter
public class BunnyStreamProperties {
    private String embedBaseUrl = "https://player.mediadelivery.net/embed";
}
