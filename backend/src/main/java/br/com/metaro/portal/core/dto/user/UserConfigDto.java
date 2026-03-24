package br.com.metaro.portal.core.dto.user;

import br.com.metaro.portal.core.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserConfigDto {
    private Long pictureId;
    private String position;
    private String name;
    private String email;
    private LocalDate birthDate;

    public UserConfigDto(User user) {
        position = user.getPosition().getName();
        name = user.getName();
        email = user.getEmail();
        birthDate = user.getBirthDate();

        if (user.getPicture() != null) pictureId = user.getPicture().getId();
    }
}
