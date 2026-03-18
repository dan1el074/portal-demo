package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.role.RoleGroupDto;
import br.com.metaro.portal.core.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADM_PANEL')")
    @GetMapping
    public ResponseEntity<List<RoleGroupDto>> findAll() {
        List<RoleGroupDto> dtos = roleService.findAll();
        return ResponseEntity.ok(dtos);
    }
}
