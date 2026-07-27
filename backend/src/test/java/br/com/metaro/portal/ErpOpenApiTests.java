package br.com.metaro.portal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "external.datasource.jdbc-url=jdbc:h2:mem:external-testdb",
        "external.datasource.driver-class-name=org.h2.Driver",
        "external.datasource.username=sa",
        "external.datasource.password="
})
class ErpOpenApiTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void keepsSharedAndStepFlowErpEndpointsPublished() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.paths['/api/erp/order/{number}']"
                ).exists())
                .andExpect(jsonPath(
                        "$.paths['/api/step-flow/erp/{orderNumber}']"
                ).exists());
    }
}
