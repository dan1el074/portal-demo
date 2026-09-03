package br.com.metaro.portal.modules.engineering.trelloIntegration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_trello_integration_record", uniqueConstraints = @UniqueConstraint(
        name = "uk_trello_integration_order_product",
        columnNames = { "order_number", "product_code" }
))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrelloIntegrationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderNumber;

    @Column(nullable = false, length = 100)
    private String orderType;

    @Column(nullable = false)
    private String client;

    @Column(nullable = false, length = 100)
    private String productCode;

    @Column(nullable = false, length = 1000)
    private String productDescription;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String seller;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime releaseAt;

    @Column(nullable = false)
    private LocalDate expectedDelivery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TrelloIntegrationStatus status;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant importedAt;

    @Column(nullable = false, length = 320)
    private String destinationEmail;

    @Column(length = 320)
    private String ccEmail;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant sentAt;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant lastResentAt;

    @Column(length = 2000)
    private String errorMessage;
}
