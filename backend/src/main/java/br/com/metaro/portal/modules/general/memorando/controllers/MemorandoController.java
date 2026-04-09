package br.com.metaro.portal.modules.general.memorando.controllers;

import br.com.metaro.portal.modules.general.memorando.dots.MemorandoDto;
import br.com.metaro.portal.modules.general.memorando.dots.MemorandoIgnoreDto;
import br.com.metaro.portal.modules.general.memorando.dots.MemorandoInsertDto;
import br.com.metaro.portal.modules.general.memorando.services.MemorandoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/memorando")
public class MemorandoController {
    @Autowired
    private MemorandoService memorandoService;

    @GetMapping
    public ResponseEntity<List<MemorandoDto>> findAll() {
        List<MemorandoDto> dtos = memorandoService.findAll();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MemorandoDto> findById(@PathVariable Long id) {
        MemorandoDto dto = memorandoService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<MemorandoDto> insert(@Valid @RequestBody MemorandoInsertDto insertDto) {
        MemorandoDto dto = memorandoService.insert(insertDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<MemorandoDto> update(@PathVariable Long id, @Valid @RequestBody MemorandoInsertDto dto) {
        MemorandoDto newDto = memorandoService.update(id, dto);
        return ResponseEntity.ok(newDto);
    }

    @PutMapping(value = "/disable/{id}")
    public ResponseEntity<MemorandoDto> disable(@PathVariable Long id) {
        MemorandoDto dto = memorandoService.disable(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/sign/{id}")
    public ResponseEntity<MemorandoDto> sign(@PathVariable Long id) {
        MemorandoDto dto = memorandoService.sign(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/rollback/{id}")
    public ResponseEntity<MemorandoDto> rollback(@PathVariable Long id) {
        MemorandoDto dto = memorandoService.rollback(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/updateSignatures/{id}")
    public ResponseEntity<MemorandoDto> updateSignatures(@PathVariable Long id, @RequestBody MemorandoIgnoreDto dto) {
        MemorandoDto memorandoDto = memorandoService.updateSignatures(id, dto);
        return ResponseEntity.ok(memorandoDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memorandoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
