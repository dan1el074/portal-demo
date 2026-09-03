package br.com.metaro.portal.modules.engineering.trelloIntegration.integration.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FoccoTrelloRecordDto {
    @JsonProperty("order_number")
    @JsonAlias({"order", "pedido"})
    private Long orderNumber;

    @JsonProperty("order_type")
    @JsonAlias({"type", "tipo", "type_order"})
    private String orderType;

    @JsonAlias({"cliente"})
    private String client;

    @JsonProperty("item_code")
    @JsonAlias({"product_code", "code", "codigo"})
    private String productCode;

    @JsonProperty("item_description")
    @JsonAlias({"product_description", "description", "descricao"})
    private String productDescription;

    @JsonProperty("item_quantity")
    @JsonAlias({"quantity", "quantidade"})
    private BigDecimal quantity;

    @JsonAlias({"vendedor", "salesperson"})
    private String seller;

    @JsonProperty("start_date")
    @JsonAlias({"start_date_data", "release_date", "release_at", "liberacao"})
    private String releaseDate;

    @JsonProperty("due_date")
    @JsonAlias({"due_date_data", "expected_delivery", "delivery_forecast", "forecast", "previsao_entrega"})
    private String expectedDelivery;
}
