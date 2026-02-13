package br.com.metaro.portal.modules.general.internalCommunication.controllers;

import br.com.metaro.portal.modules.general.internalCommunication.dots.InternalCommunicationDto;
import br.com.metaro.portal.modules.general.internalCommunication.dots.InternalCommunicationInsertDto;
import br.com.metaro.portal.modules.general.internalCommunication.services.InternalCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/ci")
public class InternalCommunicationController {
    @Autowired
    private InternalCommunicationService internalCommunicationService;

    @GetMapping
    public ResponseEntity<List<InternalCommunicationDto>> findAll() {
        List<InternalCommunicationDto> dtos = internalCommunicationService.findAll();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<InternalCommunicationDto> findById(@PathVariable Long id) {
        InternalCommunicationDto dto = internalCommunicationService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<InternalCommunicationDto> insert(@RequestBody InternalCommunicationInsertDto insertDto) {
        InternalCommunicationDto dto = internalCommunicationService.insert(insertDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<InternalCommunicationDto> update(@PathVariable Long id, @RequestBody InternalCommunicationInsertDto dto) {
        InternalCommunicationDto newDto = internalCommunicationService.update(id, dto);
        return ResponseEntity.ok(newDto);
    }

    @PutMapping(value = "/disable/{id}")
    public ResponseEntity<InternalCommunicationDto> disable(@PathVariable Long id) {
        InternalCommunicationDto dto = internalCommunicationService.disable(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/sign/{id}")
    public ResponseEntity<InternalCommunicationDto> sign(@PathVariable Long id) {
        InternalCommunicationDto dto = internalCommunicationService.sign(id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        internalCommunicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
