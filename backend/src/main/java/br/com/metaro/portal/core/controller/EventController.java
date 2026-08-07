package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.event.EventDto;
import br.com.metaro.portal.core.dto.event.EventInsertDto;
import br.com.metaro.portal.core.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDto>> findUpcoming() {
        return ResponseEntity.ok(eventService.getEvents());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POST')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventDto> insert(@ModelAttribute EventInsertDto dto) throws IOException {
        return ResponseEntity.ok(eventService.insert(dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POST')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventDto> update(@PathVariable Long id, @ModelAttribute EventInsertDto dto) throws IOException {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POST')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IOException {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
