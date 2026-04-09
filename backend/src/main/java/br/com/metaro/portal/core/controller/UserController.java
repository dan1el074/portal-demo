package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.user.*;
import br.com.metaro.portal.core.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POSITION_PANEL')")
    @GetMapping(value = "/group")
    public ResponseEntity<List<UserGroupDto>> listByPositionName() {
        List<UserGroupDto> dtos = userService.listByPositionName();
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserEditDto> findById(@PathVariable Long id) {
        UserEditDto dto = userService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/me")
    public ResponseEntity<MeDto> getMe() {
        MeDto dto = userService.getMe();
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/config")
    public ResponseEntity<UserConfigDto> getMeConfig() {
        UserConfigDto dto = userService.getConfig();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/config", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserConfigDto> updateConfig(
            @RequestParam(name = "resetPicture", required = false) String resetPicture,
            @Valid @ModelAttribute UserConfigInsertDto dto
    ) throws IOException {
        userService.updateConfig(dto, resetPicture);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UserMinDto>> insert(@Valid @ModelAttribute UserInsertDto dto) throws IOException {
        UserMinDto userMinDto = userService.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(userMinDto.getId())
                .toUri();
        List<UserMinDto> dtos = userService.findAll();
        return ResponseEntity.created(uri).body(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UserMinDto>> update(
            @PathVariable Long id,
            @RequestParam(name = "resetPicture", required = false) String resetPicture,
            @Valid @ModelAttribute UserInsertDto dto
    ) throws IOException {
        List<UserMinDto> dtos = userService.update(id, dto, resetPicture);
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @PutMapping(value = "/deactivate-user/{id}")
    public ResponseEntity<List<UserMinDto>> deactivateUser(@PathVariable Long id) {
        this.userService.deactivateUser(id);
        List<UserMinDto> dtos = userService.findAll();
        return ResponseEntity.ok(dtos);
    }
}
