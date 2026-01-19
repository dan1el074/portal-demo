package br.com.metaro.portal.core.dto;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.util.picture.PictureDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserMinDto {
    private Long id;
    private Long pictureId;
    private String name;
    private String username;
    private String position;
    private String email;
    private Boolean activated;
    private Instant updateAt;

    public UserMinDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.position = user.getPosition();
        this.email = user.getEmail();
        this.activated = user.getActivated();
        this.updateAt = user.getUpdateAt();

        if (user.getPicture() != null) {
            this.pictureId = user.getPicture().getId();
        }
    }
}
