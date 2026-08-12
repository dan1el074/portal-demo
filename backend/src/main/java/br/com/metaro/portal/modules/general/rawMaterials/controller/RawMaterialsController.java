package br.com.metaro.portal.modules.general.rawMaterials.controller;

import br.com.metaro.portal.modules.general.rawMaterials.dto.*;
import br.com.metaro.portal.modules.general.rawMaterials.service.RawMaterialsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
public class RawMaterialsController {
    private final RawMaterialsService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS')")
    @GetMapping
    public ResponseEntity<Page<RawMaterialDto>> list(Pageable pageable, @RequestParam(required = false) String search,
            @RequestParam(required = false) String category, @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "false") boolean inactive) {
        return ResponseEntity.ok(service.list(pageable, search, category, status, inactive));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS')")
    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialDto> find(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS_ADMIN','ROLE_RAW_MATERIALS_CONSULTATION')")
    @GetMapping("/summary")
    public ResponseEntity<RawMaterialSummaryDto> summary() {
        return ResponseEntity.ok(service.summary());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS')")
    @GetMapping("/categories")
    public ResponseEntity<List<RawMaterialCategoryDto>> categories() {
        return ResponseEntity.ok(service.categories());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS_ADMIN')")
    @PostMapping
    public ResponseEntity<RawMaterialDto> create(@Valid @RequestBody RawMaterialInputDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialDto> update(@PathVariable Long id, @Valid @RequestBody RawMaterialInputDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS_OPERATOR')")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<RawMaterialDto> stock(@PathVariable Long id, @Valid @RequestBody RawMaterialStockDto dto) {
        return ResponseEntity.ok(service.updateStock(id, dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS')")
    @GetMapping("/{id}/history")
    public ResponseEntity<Page<RawMaterialHistoryDto>> history(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(service.history(id, pageable));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS_ADMIN')")
    @PostMapping("/categories")
    public ResponseEntity<RawMaterialCategoryDto> createCategory(@Valid @RequestBody RawMaterialCategoryInputDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCategory(dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS_ADMIN')")
    @PutMapping("/categories/{id}")
    public ResponseEntity<RawMaterialCategoryDto> updateCategory(@Valid @RequestBody RawMaterialCategoryInputDto dto,
                                                                 @PathVariable Long id) {
        return ResponseEntity.ok(service.updateCategory(id, dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS_ADMIN')")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        service.deleteCategory(id); return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS_ADMIN')")
    @GetMapping("/access")
    public ResponseEntity<List<RawMaterialUserAccessDto>> users() {
        return ResponseEntity.ok(service.users());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS_ADMIN','ROLE_RAW_MATERIALS_OPERATOR')")
    @GetMapping("/access/me")
    public ResponseEntity<List<Long>> myAccess() {
        return ResponseEntity.ok(service.myCategoryIds());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RAW_MATERIALS_ADMIN')")
    @PutMapping("/access")
    public ResponseEntity<Void> access(@Valid @RequestBody RawMaterialAccessUpdateDto dto) {
        service.updateAccess(dto); return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/settings/history-retention")
    public ResponseEntity<RawMaterialHistoryRetentionDto> retention() {
        return ResponseEntity.ok(service.getRetention());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/settings/history-retention")
    public ResponseEntity<RawMaterialHistoryRetentionDto> retention(@Valid @RequestBody RawMaterialHistoryRetentionDto dto) {
        return ResponseEntity.ok(service.updateRetention(dto));
    }
}
