package br.com.metaro.portal.integration.probus;

import br.com.metaro.portal.core.entities.Param;
import br.com.metaro.portal.core.repositories.ParamRepository;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.integration.probus.dto.ProbusConfigDto;
import br.com.metaro.portal.integration.probus.dto.ProbusConfigUpdateDto;
import br.com.metaro.portal.integration.probus.dto.ProbusConnectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProbusConfigService {
    static final String JDBC_URL_PARAM = "probus.integration.jdbc-url";
    static final String USERNAME_PARAM = "probus.integration.username";
    static final String PASSWORD_PARAM = "probus.integration.password";

    private final ParamRepository paramRepository;

    @Value("${external.datasource.jdbc-url}")
    private String defaultJdbcUrl;
    @Value("${external.datasource.username}")
    private String defaultUsername;
    @Value("${external.datasource.password}")
    private String defaultPassword;

    @Transactional(readOnly = true)
    public ProbusConfigDto getConfig() {
        ProbusConnectionDto connection = getConnectionConfig();
        return new ProbusConfigDto(
                connection.getJdbcUrl(),
                connection.getUsername(),
                StringUtils.hasText(connection.getPassword())
        );
    }

    @Transactional
    public ProbusConfigDto updateConfig(ProbusConfigUpdateDto dto) {
        String jdbcUrl = dto.getJdbcUrl().trim();
        String username = dto.getUsername().trim();
        String currentPassword = getConnectionConfig().getPassword();
        String password = StringUtils.hasText(dto.getPassword()) ? dto.getPassword().trim() : currentPassword;

        if (!StringUtils.hasText(password)) {
            throw new UnprocessableEntityException("Informe a senha do Probus.");
        }

        saveValue(JDBC_URL_PARAM, jdbcUrl);
        saveValue(USERNAME_PARAM, username);
        saveValue(PASSWORD_PARAM, password);
        return new ProbusConfigDto(jdbcUrl, username, true);
    }

    @Transactional(readOnly = true)
    public ProbusConnectionDto getConnectionConfig() {
        return new ProbusConnectionDto(
                findValueOrDefault(JDBC_URL_PARAM, defaultJdbcUrl),
                findValueOrDefault(USERNAME_PARAM, defaultUsername),
                findValueOrDefault(PASSWORD_PARAM, defaultPassword)
        );
    }

    private String findValueOrDefault(String name, String defaultValue) {
        return paramRepository.findByName(name)
                .map(Param::getContent)
                .filter(StringUtils::hasText)
                .orElse(defaultValue);
    }

    private void saveValue(String name, String content) {
        Param param = paramRepository.findByName(name).orElseGet(() -> new Param(null, name, null));
        param.setContent(content);
        paramRepository.save(param);
    }
}
