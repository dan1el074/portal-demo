package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.user.UserInsertDto;
import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.RoleRepository;
import br.com.metaro.portal.core.repositories.UserRepository;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
        "external.datasource.jdbc-url=jdbc:h2:mem:user-service-role-validation-testdb",
        "external.datasource.driver-class-name=org.h2.Driver",
        "external.datasource.username=sa",
        "external.datasource.password="
})
class UserServiceRoleValidationTests {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Transactional
    void rejectsInactiveRoleWhenCreatingUser() {
        Role inactiveRole = findInactiveStepFlowAdminRole();
        UserInsertDto dto = validDto(inactiveRole.getId());

        assertThatThrownBy(() -> userService.insert(dto))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining(inactiveRole.getAuthority());
    }

    @Test
    @Transactional
    void rejectsInactiveRoleWhenUpdatingUser() {
        Role inactiveRole = findInactiveStepFlowAdminRole();
        User existingUser = userRepository.findAll().getFirst();
        UserInsertDto dto = validDto(inactiveRole.getId());
        dto.setPassword(null);

        assertThatThrownBy(() -> userService.update(existingUser.getId(), dto, "false"))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining(inactiveRole.getAuthority());
    }

    private Role findInactiveStepFlowAdminRole() {
        return roleRepository.findAll().stream()
                .filter(role -> "ROLE_STEP_FLOW_ADMIN".equals(role.getAuthority()))
                .findFirst()
                .orElseThrow();
    }

    private UserInsertDto validDto(Long roleId) {
        UserInsertDto dto = new UserInsertDto();
        dto.setName("Usuário de teste");
        dto.setPosition("1");
        dto.setEmail("role-validation@example.com");
        dto.setBirthDate(LocalDate.of(2000, 1, 1));
        dto.setUsername("role-validation");
        dto.setPassword("123456");
        dto.setRoles(roleId.toString());
        dto.setActivated("true");
        return dto;
    }
}
