package br.com.metaro.portal.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "externo.datasource")
    public DataSource externoDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate externoJdbcTemplate(
            @Qualifier("externoDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
