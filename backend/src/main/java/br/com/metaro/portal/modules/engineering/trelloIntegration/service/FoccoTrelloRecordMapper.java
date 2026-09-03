package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import br.com.metaro.portal.integration.focco.FoccoIntegrationException;
import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationRecord;
import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus;
import br.com.metaro.portal.modules.engineering.trelloIntegration.integration.dto.FoccoTrelloRecordDto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Component
public class FoccoTrelloRecordMapper {
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    );
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );

    public TrelloIntegrationRecord map(
            FoccoTrelloRecordDto source,
            String destinationEmail,
            String ccEmail
    ) {
        Long orderNumber = require(source.getOrderNumber(), "pedido");
        String orderType = requireText(source.getOrderType(), "tipo");
        String client = requireText(source.getClient(), "cliente");
        String productCode = requireText(source.getProductCode(), "código do produto").toUpperCase(Locale.ROOT);
        String productDescription = requireText(source.getProductDescription(), "descrição do produto");
        BigDecimal quantity = require(source.getQuantity(), "quantidade");
        String seller = requireText(source.getSeller(), "vendedor");
        LocalDateTime releaseAt = parseDateTime(source.getReleaseDate(), "liberação");
        LocalDate expectedDelivery = parseDate(source.getExpectedDelivery(), "previsão de entrega");

        TrelloIntegrationRecord record = new TrelloIntegrationRecord();
        record.setOrderNumber(orderNumber);
        record.setOrderType(orderType);
        record.setClient(client);
        record.setProductCode(productCode);
        record.setProductDescription(productDescription);
        record.setQuantity(quantity);
        record.setSeller(seller);
        record.setReleaseAt(releaseAt);
        record.setExpectedDelivery(expectedDelivery);
        record.setStatus(TrelloIntegrationStatus.PENDING);
        record.setImportedAt(Instant.now());
        record.setDestinationEmail(destinationEmail);
        record.setCcEmail(StringUtils.hasText(ccEmail) ? ccEmail : null);
        return record;
    }

    private LocalDateTime parseDateTime(String value, String field) {
        String normalized = requireText(value, field).replace(" às ", " ");
        try {
            return OffsetDateTime.parse(normalized).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            // O Focco normalmente devolve uma data local sem offset.
        }
        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
                // Tenta o próximo formato aceito pela integração.
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(normalized, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Tenta o próximo formato aceito pela integração.
            }
        }
        throw invalidField(field);
    }

    private LocalDate parseDate(String value, String field) {
        String normalized = requireText(value, field).replace(" às ", " ");
        try {
            return OffsetDateTime.parse(normalized).toLocalDate();
        } catch (DateTimeParseException ignored) {
            // Tenta os formatos sem offset abaixo.
        }
        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(normalized, formatter).toLocalDate();
            } catch (DateTimeParseException ignored) {
                // Tenta o próximo formato aceito pela integração.
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
                // Tenta o próximo formato aceito pela integração.
            }
        }
        throw invalidField(field);
    }

    private String requireText(String value, String field) {
        if (!StringUtils.hasText(value)) throw invalidField(field);
        return value.trim();
    }

    private <T> T require(T value, String field) {
        if (value == null) throw invalidField(field);
        return value;
    }

    private FoccoIntegrationException invalidField(String field) {
        return new FoccoIntegrationException("O FoccoERP retornou o campo %s vazio ou inválido.".formatted(field));
    }
}
