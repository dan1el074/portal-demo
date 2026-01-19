package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.*;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.modules.general.post.PostDto;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @PostMapping
    public ResponseEntity<List<UserMinDto>> insert(
            @RequestPart(name = "picture", required = false) MultipartFile picture,
            @RequestPart("name") String name,
            @RequestPart("position") String position,
            @RequestPart("email") String email,
            @RequestPart("birthDate") String birthDate,
            @RequestPart("username") String username,
            @RequestPart("password") String password,
            @RequestPart("roles") String roles,
            @RequestPart("activated") String activated
    ) throws IOException {
        UserMinDto userMinDto = userService.insert(new UserInsertDto(picture, name, position, email, birthDate, username,
                password, roles, activated));
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(userMinDto.getId())
                .toUri();
        List<UserMinDto> dtos = userService.findAll();
        return ResponseEntity.created(uri).body(dtos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<List<UserMinDto>> update(
            @PathVariable Long id,
            @RequestPart(name = "picture", required = false) MultipartFile picture,
            @RequestPart(name = "resetPicture", required = false) String resetPicture,
            @RequestPart("name") String name,
            @RequestPart("position") String position,
            @RequestPart("email") String email,
            @RequestPart("birthDate") String birthDate,
            @RequestPart("username") String username,
            @RequestPart(name = "password", required = false) String password,
            @RequestPart("roles") String roles,
            @RequestPart("activated") String activated
    ) throws IOException {
        UserMinDto userMinDto = userService.update(id, new UserInsertDto(picture, name, position, email, birthDate,
                username, password, roles, activated), resetPicture);
        List<UserMinDto> dtos = userService.findAll();
        return ResponseEntity.ok(dtos);
    }
}
