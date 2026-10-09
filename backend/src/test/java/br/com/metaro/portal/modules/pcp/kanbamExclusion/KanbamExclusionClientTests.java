package br.com.metaro.portal.modules.pcp.kanbamExclusion;

import br.com.metaro.portal.integration.focco.FoccoConfigService;
import br.com.metaro.portal.integration.focco.FoccoIntegrationException;
import br.com.metaro.portal.integration.focco.dto.FoccoCredentialsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KanbamExclusionClientTests {
    @Test
    void findsItemsByLotAndMapsErpFields() {
        KanbamExclusionClient client = createClient();
        MockRestServiceServer server = bindServer(client);

        server.expect(requestTo("https://focco.example/kanbam?chave=test-key&num_lote=123&orderAsc=cod_item"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andRespond(withSuccess("""
                        {
                          "value": [{
                            "num_lote": 123,
                            "id_ordem": 401614,
                            "num_ordem": 38280,
                            "cod_item": "25207",
                            "desc_item": "PC ZB EIXO SAE 1045",
                            "quantidade": 2.5
                          }],
                          "succeeded": true,
                          "failed": false,
                          "allFailed": false
                        }
                        """, MediaType.APPLICATION_JSON));

        var items = client.findByLotNumber(123);

        assertThat(items).singleElement().satisfies(item -> {
            assertThat(item.getLotNumber()).isEqualTo(123L);
            assertThat(item.getOrderId()).isEqualTo(401614L);
            assertThat(item.getOrderNumber()).isEqualTo(38280L);
            assertThat(item.getItemCode()).isEqualTo("25207");
            assertThat(item.getItemDescription()).isEqualTo("PC ZB EIXO SAE 1045");
            assertThat(item.getQuantidade()).isEqualByComparingTo("2.5");
        });
        var portalJson = new ObjectMapper().valueToTree(items.getFirst());
        assertThat(portalJson.get("lotNumber").asLong()).isEqualTo(123L);
        assertThat(portalJson.get("orderId").asLong()).isEqualTo(401614L);
        assertThat(portalJson.get("orderNumber").asLong()).isEqualTo(38280L);
        assertThat(portalJson.get("itemCode").asText()).isEqualTo("25207");
        assertThat(portalJson.get("itemDescription").asText()).isEqualTo("PC ZB EIXO SAE 1045");
        assertThat(portalJson.has("id_ordem")).isFalse();
        server.verify();
    }

    @Test
    void deletesManufacturingOrderUsingBearerToken() {
        KanbamExclusionClient client = createClient();
        MockRestServiceServer server = bindServer(client);

        server.expect(requestTo("https://focco.example/orders/401614"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andRespond(withSuccess());

        client.deleteManufacturingOrder(401614);

        server.verify();
    }

    @Test
    void exposesErpProblemDetailWhenDeletionFails() {
        KanbamExclusionClient client = createClient();
        MockRestServiceServer server = bindServer(client);

        server.expect(requestTo("https://focco.example/orders/401614"))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                  "title": "Erro ao excluir a ordem de fabricação",
                                  "detail": "A ordem possui apontamentos vinculados."
                                }
                                """));

        assertThatThrownBy(() -> client.deleteManufacturingOrder(401614))
                .isInstanceOf(FoccoIntegrationException.class)
                .hasMessageContaining("422")
                .hasMessageContaining("A ordem possui apontamentos vinculados.");
        server.verify();
    }

    private KanbamExclusionClient createClient() {
        FoccoConfigService configService = new FoccoConfigService(null) {
            @Override
            public FoccoCredentialsDto getCredentials() {
                return new FoccoCredentialsDto("https://focco.example", "test-key", "test-token");
            }
        };
        return new KanbamExclusionClient(
                configService,
                new ObjectMapper(),
                "/kanbam",
                "/orders"
        );
    }

    private MockRestServiceServer bindServer(KanbamExclusionClient client) {
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(client, "restTemplate");
        Assertions.assertNotNull(restTemplate);
        return MockRestServiceServer.bindTo(restTemplate).build();
    }
}
