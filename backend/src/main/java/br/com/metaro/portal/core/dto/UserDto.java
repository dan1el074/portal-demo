package br.com.metaro.portal.core.dto;

import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String position;
    private LocalDate birthDate;
    private String picture;
    private Boolean activated;
    private String username;
    private String password;
    private Instant createdAt;
    private Instant updateAt;
    private List<RoleDto> roles;

    public UserDto(User user) {
        id = user.getId();
        name = user.getName();
        email = user.getEmail();
        position = user.getPosition();
        birthDate = user.getBirthDate();
        picture = user.getPicture();
        activated = user.getActivated();
        username = user.getUsername();
        password = user.getPassword();
        createdAt = user.getCreatedAt();
        updateAt = user.getUpdateAt();

        roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            RoleDto roleDto = new RoleDto(role);
            roles.add(roleDto);
        }
    }
}
