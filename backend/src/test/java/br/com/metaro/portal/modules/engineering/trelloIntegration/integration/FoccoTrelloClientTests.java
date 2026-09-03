package br.com.metaro.portal.modules.engineering.trelloIntegration.integration;

import br.com.metaro.portal.integration.focco.FoccoConfigService;
import br.com.metaro.portal.integration.focco.dto.FoccoCredentialsDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FoccoTrelloClientTests {
    @Test
    void sendsAscendingStartDateFilterWithGreaterThanOrEqualOperator() {
        FoccoConfigService configService = mock(FoccoConfigService.class);
        when(configService.getCredentials())
                .thenReturn(new FoccoCredentialsDto("https://focco.example", "test-key", "test-token"));
        FoccoTrelloClient client = new FoccoTrelloClient(configService);
        ReflectionTestUtils.setField(client, "endpoint", "/portal_trello");
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(client, "restTemplate");
        Assertions.assertNotNull(restTemplate);
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        server.expect(requestTo("https://focco.example/portal_trello?Chave=test-key&orderAsc=start_date&start_date=%3E%3D01/09/2026"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andRespond(withSuccess("""
                        {
                          "value": [{
                            "order_number": 14644,
                            "order_type": "Venda",
                            "client": "Cliente",
                            "item_code": "42971",
                            "item_description": "Produto",
                            "item_quantity": 2,
                            "seller": "Vendedor",
                            "start_date": "01/09/2026 08:26",
                            "due_date": "04/09/2026"
                          }],
                          "succeeded": true,
                          "failed": false,
                          "allFailed": false
                        }
                        """, MediaType.APPLICATION_JSON));

        var records = client.findRecordsFrom(LocalDate.of(2026, 9, 1));

        assertThat(records).singleElement().satisfies(record -> {
            assertThat(record.getOrderNumber()).isEqualTo(14644L);
            assertThat(record.getProductCode()).isEqualTo("42971");
        });
        server.verify();
    }
}
