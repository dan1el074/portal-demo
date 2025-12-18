package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.MeDto;
import br.com.metaro.portal.core.dto.UserDto;
import br.com.metaro.portal.core.dto.UserInsertDto;
import br.com.metaro.portal.core.dto.UserMinDto;
import br.com.metaro.portal.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @GetMapping
    public ResponseEntity<List<UserMinDto>> findAll() {
        List<UserMinDto> dtos = userService.findAll();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/me")
    public ResponseEntity<MeDto> getMe() {
        MeDto dto = userService.getMe();
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @PostMapping
    public ResponseEntity<UserDto> insert(@RequestBody UserInsertDto insertDto) {
        UserDto dto = userService.insert(insertDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }
}
