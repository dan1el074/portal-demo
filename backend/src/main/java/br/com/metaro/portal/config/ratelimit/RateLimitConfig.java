package br.com.metaro.portal.config.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {
    @Bean
    public RateLimitFilter rateLimitFilter(RateLimitProperties properties, ObjectMapper objectMapper) {
        return new RateLimitFilter(properties, objectMapper);
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> disableAutomaticRateLimitFilterRegistration(
            RateLimitFilter rateLimitFilter
    ) {
        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>(rateLimitFilter);
        registration.setEnabled(false);
        return registration;
    }
}
