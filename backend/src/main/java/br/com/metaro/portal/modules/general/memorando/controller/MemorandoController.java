package br.com.metaro.portal.modules.general.memorando.controller;

import br.com.metaro.portal.modules.general.memorando.dto.MemorandoDto;
import br.com.metaro.portal.modules.general.memorando.dto.MemorandoIgnoreDto;
import br.com.metaro.portal.modules.general.memorando.dto.MemorandoInsertDto;
import br.com.metaro.portal.modules.general.memorando.dto.MemorandoListDto;
import br.com.metaro.portal.modules.general.memorando.service.MemorandoService;
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
    public ResponseEntity<List<MemorandoListDto>> listMemorandos() {
        List<MemorandoListDto> dtos = memorandoService.listMemorandos();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MemorandoDto> getMemorando(@PathVariable Long id) {
        MemorandoDto dto = memorandoService.getMemorando(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<MemorandoDto> createMemorando(@Valid @RequestBody MemorandoInsertDto insertDto) throws Exception {
        MemorandoDto dto = memorandoService.createMemorando(insertDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<MemorandoDto> updateMemorando(@PathVariable Long id, @Valid @RequestBody MemorandoInsertDto dto) throws Exception {
        MemorandoDto newDto = memorandoService.updateMemorando(id, dto);
        return ResponseEntity.ok(newDto);
    }

    @PutMapping(value = "/disable/{id}")
    public ResponseEntity<MemorandoDto> cancelMemorando(@PathVariable Long id) {
        MemorandoDto dto = memorandoService.cancelMemorando(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/sign/{id}")
    public ResponseEntity<MemorandoDto> signMemorando(@PathVariable Long id) throws Exception {
        MemorandoDto dto = memorandoService.signMemorando(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/rollback/{id}")
    public ResponseEntity<MemorandoDto> returnMemorandoToDraft(@PathVariable Long id) {
        MemorandoDto dto = memorandoService.returnMemorandoToDraft(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/updateSignatures/{id}")
    public ResponseEntity<MemorandoDto> refreshSignatures(@PathVariable Long id, @RequestBody MemorandoIgnoreDto dto) throws Exception {
        MemorandoDto memorandoDto = memorandoService.refreshSignatures(id, dto);
        return ResponseEntity.ok(memorandoDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteMemorando(@PathVariable Long id) {
        memorandoService.deleteMemorando(id);
        return ResponseEntity.noContent().build();
    }
}
