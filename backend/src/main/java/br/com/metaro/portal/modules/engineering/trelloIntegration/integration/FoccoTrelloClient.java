package br.com.metaro.portal.modules.engineering.trelloIntegration.integration;

import br.com.metaro.portal.integration.focco.FoccoConfigService;
import br.com.metaro.portal.integration.focco.FoccoIntegrationException;
import br.com.metaro.portal.integration.focco.dto.FoccoCredentialsDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.integration.dto.FoccoTrelloRecordDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.integration.dto.FoccoTrelloResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FoccoTrelloClient {
    private static final DateTimeFormatter QUERY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final FoccoConfigService configService;

    @Value("${focco.module.trello-integration}")
    private String endpoint;

    private final RestTemplate restTemplate = createRestTemplate();

    public List<FoccoTrelloRecordDto> findRecordsFrom(LocalDate startDate) {
        FoccoCredentialsDto credentials = configService.getCredentials();
        URI uri = UriComponentsBuilder.fromUriString(buildUrl(credentials.getBaseUrl(), endpoint))
                .queryParam("Chave", credentials.getKey())
                .queryParam("orderAsc", "start_date")
                .queryParam("start_date", ">=" + QUERY_DATE_FORMAT.format(startDate))
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(credentials.getToken());

        try {
            ResponseEntity<FoccoTrelloResponseDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    FoccoTrelloResponseDto.class
            );
            return validateResponse(response.getBody());
        } catch (RestClientException exception) {
            throw new FoccoIntegrationException("Não foi possível consultar os pedidos para o Trello no FoccoERP.", exception);
        }
    }

    private List<FoccoTrelloRecordDto> validateResponse(FoccoTrelloResponseDto response) {
        if (response == null) {
            throw new FoccoIntegrationException("O FoccoERP retornou uma resposta vazia.");
        }
        if (response.isFailed() || response.isAllFailed()) {
            String detail = StringUtils.hasText(response.getErrorMessage())
                    ? response.getErrorMessage()
                    : response.getBaseErrorMessage();
            String message = StringUtils.hasText(detail)
                    ? "O FoccoERP não concluiu a consulta: " + detail
                    : "O FoccoERP não concluiu a consulta.";
            throw new FoccoIntegrationException(message);
        }
        return response.getValue() == null ? List.of() : response.getValue();
    }

    private String buildUrl(String baseUrl, String moduleEndpoint) {
        String normalizedBaseUrl = baseUrl.replaceAll("/+$", "");
        String normalizedEndpoint = moduleEndpoint.replaceAll("^/+", "");
        return normalizedBaseUrl + "/" + normalizedEndpoint;
    }

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(30_000);
        return new RestTemplate(requestFactory);
    }
}
