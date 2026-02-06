package br.com.metaro.portal.modules.general.internalControl;

import br.com.metaro.portal.modules.general.internalControl.dots.InternalControlDto;
import br.com.metaro.portal.modules.general.internalControl.dots.InternalControlInsertDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/ci")
public class InternalControlController {
    @Autowired
    private InternalControlService internalControlService;

    @GetMapping
    public ResponseEntity<List<InternalControlDto>> findAll() {
        List<InternalControlDto> dtos = internalControlService.findAll();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<InternalControlDto> findById(@PathVariable Long id) {
        InternalControlDto dto = internalControlService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<InternalControlDto> insert(@RequestBody InternalControlInsertDto insertDto) {
        InternalControlDto dto = internalControlService.insert(insertDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<InternalControlDto> update(@PathVariable Long id, @RequestBody InternalControlInsertDto insertDto) {
        InternalControlDto dto = internalControlService.update(id, insertDto);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/disable/{id}")
    public ResponseEntity<InternalControlDto> disable(@PathVariable Long id) {
        InternalControlDto dto = internalControlService.disable(id);
        return ResponseEntity.ok(dto);
    }

}
