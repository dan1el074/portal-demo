package br.com.metaro.portal.core.dto.position;

import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PositionDto {
    private Long id;
    private String name;
    private Boolean activated;
    private List<UserSummaryDto> manangers;
    private Instant updatedAt;
    private Instant createdAt;

    public PositionDto(Position position) {
        id = position.getId();
        name = position.getName();
        activated = position.getActivated();
        updatedAt = position.getUpdatedAt();
        createdAt = position.getCreatedAt();
        manangers = new ArrayList<>();

        if (position.getManangers() != null) {
            for (User mananger : position.getManangers()) {
                manangers.add(new UserSummaryDto(mananger));
            }
        }
    }
}
