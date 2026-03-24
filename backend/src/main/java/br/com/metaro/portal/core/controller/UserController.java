package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.user.*;
import br.com.metaro.portal.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping(value = "/config")
    public ResponseEntity<UserConfigDto> updateConfig(
            @RequestPart(name = "picture", required = false) MultipartFile picture,
            @RequestPart(name = "resetPicture", required = false) String resetPicture,
            @RequestPart("name") String name,
            @RequestPart("email") String email,
            @RequestPart("birthDate") String birthDate,
            @RequestPart(name = "password", required = false) String password
    ) throws IOException {
        userService.updateConfig(new UserConfigInsertDto(picture, name, email, birthDate, password), resetPicture);
        return ResponseEntity.noContent().build();
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
            @RequestPart("activated") String activated,
            @RequestPart(name = "supportToken", required = false) String supportToken
    ) throws IOException {
        UserMinDto userMinDto = userService.insert(new UserInsertDto(picture, name, position, email, birthDate, username,
                password, roles, activated, supportToken));
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
            @RequestPart("activated") String activated,
            @RequestPart(name ="supportToken", required = false) String supportToken
    ) throws IOException {
        List<UserMinDto> dtos = userService.update(id, new UserInsertDto(picture, name, position, email, birthDate,
                username, password, roles, activated, supportToken), resetPicture);
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
