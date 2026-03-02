package br.com.metaro.portal.core.dto;

import br.com.metaro.portal.core.entities.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PositionDto {
    private Long id;
    private String name;
    private UserSummaryDto mananger;
    private Instant updatedAt;
    private Instant createdAt;

    public PositionDto(Position position) {
        id = position.getId();
        name = position.getName();
        updatedAt = position.getUpdatedAt();
        createdAt = position.getCreatedAt();

        if (position.getMananger() != null) mananger = new UserSummaryDto(position.getMananger());
    }
}
