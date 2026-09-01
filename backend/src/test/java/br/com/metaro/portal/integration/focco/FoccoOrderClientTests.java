package br.com.metaro.portal.integration.focco;

import br.com.metaro.portal.integration.focco.dto.FoccoCredentialsDto;
import br.com.metaro.portal.util.erp.ErpSource;
import br.com.metaro.portal.util.erp.dto.ErpOrderDto;
import br.com.metaro.portal.util.erp.dto.ErpOrderLineDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FoccoOrderClientTests {
    @Test
    void mapsFoccoResponseAndSendsConfiguredCredentials() {
        FoccoConfigService configService = mock(FoccoConfigService.class);
        when(configService.getCredentials())
                .thenReturn(new FoccoCredentialsDto("https://focco.example", "test-key", "test-token"));

        FoccoOrderClient client = new FoccoOrderClient(
                configService,
                "/orders",
                "/order"
        );
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(client, "restTemplate");
        Assertions.assertNotNull(restTemplate);
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        server.expect(requestTo("https://focco.example/orders?Chave=test-key&order_number=149"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andRespond(withSuccess("""
                        {
                          "value": [{
                            "order_number": 149,
                            "client": "9152 - Cliente",
                            "cnpj": "04544000000103",
                            "phone": "38 2018.295",
                            "seller": "Vendedor",
                            "start_date_data": "2026-08-28T10:27:00",
                            "due_date_data": "2026-08-31T00:00:00",
                            "adress": "Endereço",
                            "subtotal": 103928.0,
                            "discount": 0.0,
                            "total": 103928.0,
                            "item_code": "60289",
                            "item_description": "Item",
                            "item_unit_value": 12991.0,
                            "item_unit": "un",
                            "item_quantity": 8.0
                          }],
                          "succeeded": true,
                          "failed": false,
                          "allFailed": false,
                          "errorMessage": ""
                        }
                        """, MediaType.APPLICATION_JSON));

        ErpOrderDto order = client.findProductionOrderByNumber(149);

        assertThat(order.getSource()).isEqualTo(ErpSource.FOCCO);
        assertThat(order.getNumber()).isEqualTo(149);
        assertThat(order.getAddress()).isEqualTo("Endereço");
        assertThat(order.getStartDate()).isEqualTo(LocalDate.of(2026, 8, 28));
        assertThat(order.getItems()).singleElement().satisfies(item -> {
            assertThat(item.getCode()).isEqualTo("60289");
            assertThat(item.getQuantity()).isEqualTo(8);
        });
        server.verify();
    }

    @Test
    void mapsMemorandoOrderLinesFromOrderEndpoint() {
        FoccoConfigService configService = mock(FoccoConfigService.class);
        when(configService.getCredentials())
                .thenReturn(new FoccoCredentialsDto("https://focco.example", "test-key", "test-token"));

        FoccoOrderClient client = new FoccoOrderClient(
                configService,
                "/orders",
                "/order"
        );
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(client, "restTemplate");
        Assertions.assertNotNull(restTemplate);
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        server.expect(requestTo("https://focco.example/order?Chave=test-key&order_number=140"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andRespond(withSuccess("""
                        {
                          "value": [{
                            "order_number": 140,
                            "client": "9144 - Cliente",
                            "item_code": "60060",
                            "item_description": "Item do memorando"
                          }],
                          "succeeded": true,
                          "failed": false,
                          "allFailed": false,
                          "errorMessage": ""
                        }
                        """, MediaType.APPLICATION_JSON));

        List<ErpOrderLineDto> lines = client.findOrderLinesByNumber(140);

        assertThat(lines).singleElement().satisfies(line -> {
            assertThat(line.getNumber()).isEqualTo(140);
            assertThat(line.getClient()).isEqualTo("9144 - Cliente");
            assertThat(line.getItem()).isEqualTo("60060 - Item do memorando");
            assertThat(line.getSource()).isEqualTo(ErpSource.FOCCO);
        });
        server.verify();
    }
}
