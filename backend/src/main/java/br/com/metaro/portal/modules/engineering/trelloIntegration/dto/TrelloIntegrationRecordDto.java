package br.com.metaro.portal.modules.engineering.trelloIntegration.dto;

import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus;
import br.com.metaro.portal.modules.engineering.trelloIntegration.repository.projections.TrelloIntegrationRecordProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrelloIntegrationRecordDto {
    private Long id;
    private String order;
    private String orderType;
    private String client;
    private String code;
    private String description;
    private BigDecimal quantity;
    private String seller;
    private LocalDateTime releaseDate;
    private LocalDate expectedDelivery;
    private TrelloIntegrationStatus status;
    private String statusLabel;
    private Instant importedAt;
    private String destinationEmail;
    private Instant sentAt;
    private Instant lastResentAt;
    private String errorMessage;

    public TrelloIntegrationRecordDto(TrelloIntegrationRecordProjection projection) {
        this(
                projection.getId(),
                String.valueOf(projection.getOrderNumber()),
                projection.getOrderType(),
                projection.getClient(),
                projection.getProductCode(),
                projection.getProductDescription(),
                projection.getQuantity(),
                projection.getSeller(),
                projection.getReleaseAt(),
                projection.getExpectedDelivery(),
                projection.getStatus(),
                projection.getStatus().getDescription(),
                projection.getImportedAt(),
                projection.getDestinationEmail(),
                projection.getSentAt(),
                projection.getLastResentAt(),
                projection.getErrorMessage()
        );
    }
}
