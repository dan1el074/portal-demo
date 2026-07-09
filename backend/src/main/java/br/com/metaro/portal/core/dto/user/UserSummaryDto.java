package br.com.metaro.portal.core.dto.user;

import br.com.metaro.portal.core.dto.position.PositionMinDto;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.util.picture.dto.PictureMinDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserSummaryDto {
    private Long id;
    private String name;
    private PositionMinDto position;
    private PictureMinDto picture;

    public UserSummaryDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.position = new PositionMinDto(user.getPosition());

        if (user.getPicture() != null) {
            this.picture = new PictureMinDto(user.getPicture());
        }
    }

    public UserSummaryDto(Long id, String name, Long pictureId, String positionName) {
        this.id = id;
        this.name = name;
        this.picture = new PictureMinDto(pictureId);
        this.position = new PositionMinDto(positionName);
    }
}
