package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationConsultResultDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationRecordDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationSettingsDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.dto.TrelloIntegrationSummaryDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationRecord;
import br.com.metaro.portal.modules.engineering.trelloIntegration.integration.FoccoTrelloClient;
import br.com.metaro.portal.modules.engineering.trelloIntegration.integration.dto.FoccoTrelloRecordDto;
import br.com.metaro.portal.modules.engineering.trelloIntegration.repository.TrelloIntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TrelloIntegrationService {
    private static final Map<String, String> SORT_FIELDS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("order", "orderNumber"),
            Map.entry("orderType", "orderType"),
            Map.entry("client", "client"),
            Map.entry("code", "productCode"),
            Map.entry("description", "productDescription"),
            Map.entry("quantity", "quantity"),
            Map.entry("seller", "seller"),
            Map.entry("releaseDate", "releaseAt"),
            Map.entry("expectedDelivery", "expectedDelivery"),
            Map.entry("status", "status"),
            Map.entry("importedAt", "importedAt")
    );

    private final TrelloIntegrationRepository repository;
    private final TrelloIntegrationSettingsService settingsService;
    private final FoccoTrelloClient foccoClient;
    private final FoccoTrelloRecordMapper mapper;
    private final TrelloIntegrationEmailScheduler emailScheduler;
    private final TrelloIntegrationEmailSender emailSender;

    @Transactional(readOnly = true)
    public Page<TrelloIntegrationRecordDto> list(Pageable pageable, String search) {
        String normalizedSearch = StringUtils.hasText(search) ? search.trim() : "";
        return repository.search(normalizePageable(pageable), normalizedSearch)
                .map(TrelloIntegrationRecordDto::new);
    }

    @Transactional(readOnly = true)
    public TrelloIntegrationRecordDto findById(Long id) {
        return repository.findProjectedById(id)
                .map(TrelloIntegrationRecordDto::new)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public TrelloIntegrationSummaryDto summary() {
        return new TrelloIntegrationSummaryDto(repository.summarize());
    }

    @Transactional
    public TrelloIntegrationConsultResultDto consultErp() {
        TrelloIntegrationSettingsDto settings = settingsService.getSettings();
        settingsService.validateReadyForConsult(settings);

        LocalDate startDate = LocalDate.now().minusDays(settings.getErpLookbackDays());
        List<FoccoTrelloRecordDto> erpRecords = foccoClient.findRecordsFrom(startDate);
        Map<String, TrelloIntegrationRecord> distinctRecords = new LinkedHashMap<>();
        for (FoccoTrelloRecordDto erpRecord : erpRecords) {
            TrelloIntegrationRecord record = mapper.map(
                    erpRecord,
                    settings.getDestinationEmail(),
                    settings.getCcEmail()
            );
            distinctRecords.putIfAbsent(recordKey(record), record);
        }

        Instant retentionThreshold = Instant.now().minus(settings.getRetentionDays(), ChronoUnit.DAYS);
        int removed = repository.deleteImportedBefore(retentionThreshold);

        int imported = 0;
        int ignored = erpRecords.size() - distinctRecords.size();
        List<TrelloIntegrationRecord> recordsToSave = new ArrayList<>();

        for (TrelloIntegrationRecord record : distinctRecords.values()) {
            if (repository.existsByOrderNumberAndProductCode(
                    record.getOrderNumber(), record.getProductCode()
            )) {
                ignored++;
                continue;
            }

            recordsToSave.add(record);
            imported++;
        }

        List<Long> scheduledIds = repository.saveAllAndFlush(recordsToSave).stream()
                .map(TrelloIntegrationRecord::getId)
                .toList();
        emailScheduler.schedule(scheduledIds);

        return new TrelloIntegrationConsultResultDto(
                erpRecords.size(), imported, ignored, scheduledIds.size(), removed
        );
    }

    public TrelloIntegrationRecordDto resend(Long id) {
        TrelloIntegrationRecord record = repository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        TrelloIntegrationSettingsDto settings = settingsService.getSettings();
        settingsService.validateReadyForConsult(settings);

        record.setDestinationEmail(settings.getDestinationEmail());
        record.setCcEmail(StringUtils.hasText(settings.getCcEmail()) ? settings.getCcEmail() : null);
        emailSender.resend(record);

        return findById(id);
    }

    private String recordKey(TrelloIntegrationRecord record) {
        return record.getOrderNumber() + "|" + record.getProductCode().toLowerCase();
    }

    private Pageable normalizePageable(Pageable pageable) {
        int size = Math.clamp(pageable.getPageSize(), 1, 100);
        List<Sort.Order> orders = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            String property = SORT_FIELDS.get(order.getProperty());
            if (property != null) orders.add(new Sort.Order(order.getDirection(), property));
        });
        if (orders.isEmpty()) orders.add(Sort.Order.desc("importedAt"));
        if (orders.stream().noneMatch(order -> order.getProperty().equals("id"))) {
            orders.add(Sort.Order.desc("id"));
        }
        return PageRequest.of(pageable.getPageNumber(), size, Sort.by(orders));
    }
}
