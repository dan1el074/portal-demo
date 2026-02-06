package br.com.metaro.portal.core.dto;

import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserEditDto {
    private Long id;
    private Long pictureId;
    private String name;
    private Long positionId;
    private String email;
    private LocalDate birthDate;
    private String username;
    private List<Long> roles;
    private Boolean activated;

    public UserEditDto(User user) {
        id = user.getId();
        name = user.getName();
        positionId = user.getPosition().getId();
        email = user.getEmail();
        birthDate = user.getBirthDate();
        username = user.getUsername();
        activated = user.getActivated();
        roles = new ArrayList<>();

        if (user.getPicture() != null) {
            pictureId = user.getPicture().getId();
        }

        for (Role role : user.getRoles()) {
            roles.add(role.getId());
        }
    }
}
