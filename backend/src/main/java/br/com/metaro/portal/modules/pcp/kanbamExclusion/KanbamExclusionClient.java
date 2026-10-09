package br.com.metaro.portal.modules.pcp.kanbamExclusion;

import br.com.metaro.portal.integration.focco.FoccoConfigService;
import br.com.metaro.portal.integration.focco.FoccoIntegrationException;
import br.com.metaro.portal.integration.focco.dto.FoccoCredentialsDto;
import br.com.metaro.portal.modules.pcp.kanbamExclusion.dto.FoccoKanbamResponseDto;
import br.com.metaro.portal.modules.pcp.kanbamExclusion.dto.KanbamItemDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
public class KanbamExclusionClient {
    private final FoccoConfigService configService;
    private final ObjectMapper objectMapper;
    private final String lotEndpoint;
    private final String manufacturingOrdersEndpoint;
    private final RestTemplate restTemplate;

    public KanbamExclusionClient(
            FoccoConfigService configService,
            ObjectMapper objectMapper,
            @Value("${focco.module.kanbam-lot}") String lotEndpoint,
            @Value("${focco.module.manufacturing-orders}") String manufacturingOrdersEndpoint
    ) {
        this.configService = configService;
        this.objectMapper = objectMapper;
        this.lotEndpoint = lotEndpoint;
        this.manufacturingOrdersEndpoint = manufacturingOrdersEndpoint;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(30_000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public List<KanbamItemDto> findByLotNumber(long lotNumber) {
        FoccoCredentialsDto credentials = configService.getCredentials();
        URI uri = UriComponentsBuilder.fromUriString(buildUrl(credentials.getBaseUrl(), lotEndpoint))
                .queryParam("chave", credentials.getKey())
                .queryParam("num_lote", lotNumber)
                .queryParam("orderAsc", "cod_item")
                .build()
                .encode()
                .toUri();

        try {
            ResponseEntity<FoccoKanbamResponseDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    authorizedEntity(credentials),
                    FoccoKanbamResponseDto.class
            );
            return validateResponse(response.getBody());
        } catch (HttpStatusCodeException exception) {
            throw integrationFailure("consultar o lote no FoccoERP", exception);
        } catch (RestClientException exception) {
            throw new FoccoIntegrationException("Não foi possível consultar o lote no FoccoERP.", exception);
        }
    }

    public void deleteManufacturingOrder(long orderId) {
        FoccoCredentialsDto credentials = configService.getCredentials();
        URI uri = UriComponentsBuilder.fromUriString(buildUrl(credentials.getBaseUrl(), manufacturingOrdersEndpoint))
                .pathSegment(Long.toString(orderId))
                .build()
                .encode()
                .toUri();

        try {
            restTemplate.exchange(uri, HttpMethod.DELETE, authorizedEntity(credentials), Void.class);
        } catch (HttpStatusCodeException exception) {
            throw integrationFailure("excluir a ordem de fabricação " + orderId, exception);
        } catch (RestClientException exception) {
            throw new FoccoIntegrationException(
                    "Não foi possível excluir a ordem de fabricação " + orderId + " no FoccoERP.",
                    exception
            );
        }
    }

    private HttpEntity<Void> authorizedEntity(FoccoCredentialsDto credentials) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(credentials.getToken());
        return new HttpEntity<>(headers);
    }

    private List<KanbamItemDto> validateResponse(FoccoKanbamResponseDto response) {
        if (response == null) {
            throw new FoccoIntegrationException("O FoccoERP retornou uma resposta vazia.");
        }
        if (response.isFailed() || response.isAllFailed()) {
            String detail = StringUtils.hasText(response.getErrorMessage())
                    ? response.getErrorMessage()
                    : response.getBaseErrorMessage();
            String message = StringUtils.hasText(detail)
                    ? "O FoccoERP não concluiu a consulta: " + detail
                    : "O FoccoERP não concluiu a consulta do lote.";
            throw new FoccoIntegrationException(message);
        }
        return response.getValue() == null ? List.of() : response.getValue();
    }

    private FoccoIntegrationException integrationFailure(String action, HttpStatusCodeException exception) {
        String detail = extractProblemDetail(exception.getResponseBodyAsString());
        String status = exception.getStatusCode().value() + " " + exception.getStatusText();
        String message = "O FoccoERP recusou a operação ao " + action + " (" + status + ").";
        if (StringUtils.hasText(detail)) {
            message += " " + detail;
        }
        return new FoccoIntegrationException(message, exception);
    }

    private String extractProblemDetail(String responseBody) {
        if (!StringUtils.hasText(responseBody)) {
            return "";
        }
        try {
            JsonNode body = objectMapper.readTree(responseBody);
            if (body.hasNonNull("detail") && StringUtils.hasText(body.get("detail").asText())) {
                return body.get("detail").asText();
            }
            if (body.hasNonNull("title") && StringUtils.hasText(body.get("title").asText())) {
                return body.get("title").asText();
            }
        } catch (Exception ignored) {
            // O corpo de erro do ERP nem sempre é JSON; o status HTTP ainda é preservado na mensagem.
        }
        return "";
    }

    private String buildUrl(String baseUrl, String endpoint) {
        String normalizedBaseUrl = baseUrl.replaceAll("/+$", "");
        String normalizedEndpoint = endpoint.replaceAll("^/+", "");
        return normalizedBaseUrl + "/" + normalizedEndpoint;
    }
}
