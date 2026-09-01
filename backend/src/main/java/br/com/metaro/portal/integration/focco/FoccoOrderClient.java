package br.com.metaro.portal.integration.focco;

import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.integration.focco.dto.FoccoCredentialsDto;
import br.com.metaro.portal.integration.focco.dto.FoccoOrderLineDto;
import br.com.metaro.portal.integration.focco.dto.FoccoOrderResponseDto;
import br.com.metaro.portal.util.erp.ErpSource;
import br.com.metaro.portal.util.erp.dto.ErpOrderDto;
import br.com.metaro.portal.util.erp.dto.ErpOrderItemDto;
import br.com.metaro.portal.util.erp.dto.ErpOrderLineDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
public class FoccoOrderClient {
    private final FoccoConfigService configService;
    private final String stepFlowEndpoint;
    private final String memorandoEndpoint;
    private final RestTemplate restTemplate;

    public FoccoOrderClient(
            FoccoConfigService configService,
            @Value("${focco.module.step-flow}") String stepFlowEndpoint,
            @Value("${focco.module.memorando}") String memorandoEndpoint
    ) {
        this.configService = configService;
        this.stepFlowEndpoint = stepFlowEndpoint;
        this.memorandoEndpoint = memorandoEndpoint;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(30_000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public ErpOrderDto findProductionOrderByNumber(int orderNumber) {
        return mapProductionOrder(requestOrder(stepFlowEndpoint, orderNumber));
    }

    public List<ErpOrderLineDto> findOrderLinesByNumber(int orderNumber) {
        FoccoOrderResponseDto response = requestOrder(memorandoEndpoint, orderNumber);
        List<FoccoOrderLineDto> lines = response.getValue();
        if (lines == null || lines.isEmpty()) {
            return List.of();
        }

        return lines.stream()
                .map(line -> new ErpOrderLineDto(
                        line.getOrderNumber(),
                        line.getClient(),
                        line.getItemCode() + " - " + line.getItemDescription(),
                        ErpSource.FOCCO
                ))
                .toList();
    }

    private FoccoOrderResponseDto requestOrder(String endpoint, int orderNumber) {
        FoccoCredentialsDto credentials = configService.getCredentials();
        URI uri = UriComponentsBuilder.fromUriString(buildUrl(credentials.getBaseUrl(), endpoint))
                .queryParam("Chave", credentials.getKey())
                .queryParam("order_number", orderNumber)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(credentials.getToken());

        try {
            ResponseEntity<FoccoOrderResponseDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    FoccoOrderResponseDto.class
            );
            return validateResponse(response.getBody());
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException();
        } catch (RestClientException exception) {
            throw new FoccoIntegrationException("Não foi possível consultar o FoccoERP.", exception);
        }
    }

    private String buildUrl(String baseUrl, String endpoint) {
        String normalizedBaseUrl = baseUrl.replaceAll("/+$", "");
        String normalizedEndpoint = endpoint.replaceAll("^/+", "");
        return normalizedBaseUrl + "/" + normalizedEndpoint;
    }

    private FoccoOrderResponseDto validateResponse(FoccoOrderResponseDto response) {
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

        return response;
    }

    private ErpOrderDto mapProductionOrder(FoccoOrderResponseDto response) {
        List<FoccoOrderLineDto> lines = response.getValue();
        if (lines == null || lines.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        FoccoOrderLineDto first = lines.getFirst();
        ErpOrderDto order = new ErpOrderDto(
                first.getOrderNumber(),
                first.getClient(),
                first.getCnpj(),
                first.getPhone(),
                first.getSeller(),
                first.getStartDate() == null ? null : first.getStartDate().toLocalDate(),
                first.getDueDate() == null ? null : first.getDueDate().toLocalDate(),
                first.getAddress(),
                first.getSubtotal(),
                first.getDiscount(),
                first.getTotal()
        );
        order.setSource(ErpSource.FOCCO);

        for (FoccoOrderLineDto line : lines) {
            try {
                order.addItem(new ErpOrderItemDto(
                        line.getItemCode(),
                        line.getItemDescription(),
                        line.getItemUnitValue(),
                        line.getItemUnit(),
                        line.getItemQuantity().intValueExact(),
                        0
                ));
            } catch (ArithmeticException | NullPointerException exception) {
                throw new FoccoIntegrationException(
                        "O FoccoERP retornou uma quantidade inválida para o item " + line.getItemCode() + ".",
                        exception
                );
            }
        }

        return order;
    }
}
