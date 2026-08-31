package br.com.metaro.portal.integration.probus;

import br.com.metaro.portal.integration.probus.dto.ProbusConnectionDto;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.datasource.AbstractDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProbusDataSource extends AbstractDataSource {
    private final ObjectProvider<ProbusConfigService> configServiceProvider;
    private final String driverClassName;

    public ProbusDataSource(
            ObjectProvider<ProbusConfigService> configServiceProvider,
            String driverClassName
    ) {
        this.configServiceProvider = configServiceProvider;
        this.driverClassName = driverClassName;
    }

    @Override
    public Connection getConnection() throws SQLException {
        ProbusConnectionDto config = configServiceProvider.getObject().getConnectionConfig();
        loadDriver();
        return DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(), config.getPassword());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        ProbusConnectionDto config = configServiceProvider.getObject().getConnectionConfig();
        loadDriver();
        return DriverManager.getConnection(config.getJdbcUrl(), username, password);
    }

    private void loadDriver() throws SQLException {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException exception) {
            throw new SQLException("Driver JDBC do Probus não encontrado.", exception);
        }
    }
}
