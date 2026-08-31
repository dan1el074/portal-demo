package br.com.metaro.portal.config;

import br.com.metaro.portal.integration.probus.ProbusConfigService;
import br.com.metaro.portal.integration.probus.ProbusDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ExternalDataSourceConfig {
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier("externalDataSource")
    public DataSource externalDataSource(
            ObjectProvider<ProbusConfigService> configServiceProvider,
            @Value("${external.datasource.driver-class-name}") String driverClassName
    ) {
        return new ProbusDataSource(configServiceProvider, driverClassName);
    }

    @Bean
    public JdbcTemplate externalJdbcTemplate(@Qualifier("externalDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
