package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.PositionDto;
import br.com.metaro.portal.core.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/position")
public class PositionController {
    @Autowired
    private PositionService positionService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @GetMapping
    public ResponseEntity<List<PositionDto>> findAll() {
        List<PositionDto> dtos = positionService.findAll();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PositionDto> findById(@PathVariable Long id) {
        PositionDto dto = positionService.findById(id);
        return ResponseEntity.ok(dto);
    }
}
