package br.com.metaro.portal.modules.engineering.trelloIntegration.repository.projections;

import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TrelloIntegrationRecordProjection {
    Long getId();
    Long getOrderNumber();
    String getOrderType();
    String getClient();
    String getProductCode();
    String getProductDescription();
    BigDecimal getQuantity();
    String getSeller();
    LocalDateTime getReleaseAt();
    LocalDate getExpectedDelivery();
    TrelloIntegrationStatus getStatus();
    Instant getImportedAt();
    String getDestinationEmail();
    Instant getSentAt();
    Instant getLastResentAt();
    String getErrorMessage();
}
