package br.com.metaro.portal.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String JWT_SCHEME = "bearer-jwt";

    @Bean
    public OpenAPI portalOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portal Metaro")
                        .description("API REST do Portal Metaro")
                        .version("v1")
                        .contact(new Contact()
                                .name("Acessar")
                                .url("http://portal.metaro.com.br")))
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME))
                .components(new Components().addSecuritySchemes(
                        JWT_SCHEME,
                        new SecurityScheme()
                                .name(JWT_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }

    @Bean
    public GroupedOpenApi coreApi() {
        return GroupedOpenApi.builder()
                .group("core")
                .displayName("Core")
                .pathsToMatch(
                        "/api/info/**",
                        "/api/notifications/**",
                        "/api/position/**",
                        "/api/request-access/**",
                        "/api/role/**",
                        "/api/user/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi modulesApi() {
        return GroupedOpenApi.builder()
                .group("modules")
                .displayName("Modulos")
                .pathsToMatch(
                        "/api/memorando/**",
                        "/api/post/**",
                        "/api/step-flow/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi utilitiesApi() {
        return GroupedOpenApi.builder()
                .group("utilities")
                .displayName("Utilitarios")
                .pathsToMatch(
                        "/api/erp/**",
                        "/api/file/**",
                        "/api/pdf/**",
                        "/images/**"
                )
                .build();
    }
}
