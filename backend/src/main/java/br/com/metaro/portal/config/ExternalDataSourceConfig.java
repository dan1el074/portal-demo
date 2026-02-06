package br.com.metaro.portal.config;

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

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier("externalDataSource")
    @ConfigurationProperties(prefix = "external.datasource")
    public DataSource externalDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate externalJdbcTemplate(
            @Qualifier("externalDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
