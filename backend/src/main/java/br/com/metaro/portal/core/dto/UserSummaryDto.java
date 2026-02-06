package br.com.metaro.portal.core.dto;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.util.picture.PictureDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserSummaryDto {
    private Long id;
    private String name;
    private PositionDto position;
    private PictureDto picture;

    public UserSummaryDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.position = new PositionDto(user.getPosition());

        if (user.getPicture() != null) {
            this.picture = new PictureDto(user.getPicture());
        }
    }
}
