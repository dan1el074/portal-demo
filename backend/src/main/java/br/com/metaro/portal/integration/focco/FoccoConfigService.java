package br.com.metaro.portal.integration.focco;

import br.com.metaro.portal.core.entities.Param;
import br.com.metaro.portal.core.repositories.ParamRepository;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.integration.focco.dto.FoccoConfigDto;
import br.com.metaro.portal.integration.focco.dto.FoccoConfigUpdateDto;
import br.com.metaro.portal.integration.focco.dto.FoccoCredentialsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FoccoConfigService {
    static final String KEY_PARAM = "focco.integration.key";
    static final String TOKEN_PARAM = "focco.integration.token";

    private final ParamRepository paramRepository;

    @Transactional(readOnly = true)
    public FoccoConfigDto getConfig() {
        String key = findValue(KEY_PARAM);
        return new FoccoConfigDto(key, StringUtils.hasText(findValue(TOKEN_PARAM)));
    }

    @Transactional
    public FoccoConfigDto updateConfig(FoccoConfigUpdateDto dto) {
        String key = dto.getKey().trim();
        String currentToken = findValue(TOKEN_PARAM);
        String newToken = StringUtils.hasText(dto.getToken()) ? dto.getToken().trim() : currentToken;

        if (!StringUtils.hasText(newToken)) {
            throw new UnprocessableEntityException("Informe o token do FoccoERP.");
        }

        saveValue(KEY_PARAM, key);
        saveValue(TOKEN_PARAM, newToken);
        return new FoccoConfigDto(key, true);
    }

    @Transactional(readOnly = true)
    public FoccoCredentialsDto getCredentials() {
        String key = findValue(KEY_PARAM);
        String token = findValue(TOKEN_PARAM);
        if (!StringUtils.hasText(key) || !StringUtils.hasText(token)) {
            throw new UnprocessableEntityException("A integração com o FoccoERP não está configurada.");
        }
        return new FoccoCredentialsDto(key, token);
    }

    private String findValue(String name) {
        return paramRepository.findByName(name).map(Param::getContent).orElse("");
    }

    private void saveValue(String name, String content) {
        Param param = paramRepository.findByName(name).orElseGet(() -> new Param(null, name, null));
        param.setContent(content);
        paramRepository.save(param);
    }
}
