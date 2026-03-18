package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.position.PositionDto;
import br.com.metaro.portal.core.dto.position.PositionFormImputDto;
import br.com.metaro.portal.core.dto.position.PositionMinDto;
import br.com.metaro.portal.core.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/position")
public class PositionController {
    @Autowired
    private PositionService positionService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POSITION_PANEL')")
    @GetMapping
    public ResponseEntity<List<PositionDto>> findAll() {
        List<PositionDto> dtos = positionService.findAll();
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @GetMapping(value = "/min")
    public ResponseEntity<List<PositionMinDto>> list() {
        List<PositionMinDto> dtos = positionService.list();
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POSITION_PANEL')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PositionDto> findById(@PathVariable Long id) {
        PositionDto dto = positionService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POSITION_PANEL')")
    @PostMapping
    public ResponseEntity<List<PositionDto>> insert(@RequestBody PositionFormImputDto dto) {
        List<PositionDto> dtos = positionService.insert(dto);
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POSITION_PANEL')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<List<PositionDto>> update(@PathVariable Long id, @RequestBody PositionFormImputDto dto) {
        List<PositionDto> dtos = positionService.update(id, dto);
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POSITION_PANEL')")
    @PutMapping(value = "/deactive/{id}")
    public ResponseEntity<List<PositionDto>> deactive(@PathVariable Long id) {
        List<PositionDto> dtos = positionService.deactive(id);
        return ResponseEntity.ok(dtos);
    }
}
