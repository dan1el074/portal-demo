package br.com.metaro.portal.config.ratelimit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Component
@Validated
@ConfigurationProperties(prefix = "security.rate-limit")
@Getter
@Setter
public class RateLimitProperties {
    private boolean enabled = true;

    @Min(1)
    private long capacity = 100;

    @NotNull
    private Duration refillPeriod = Duration.ofMinutes(1);

    @Min(1)
    private long maxClients = 1_000;
}
