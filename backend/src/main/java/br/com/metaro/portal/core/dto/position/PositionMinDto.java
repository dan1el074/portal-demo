package br.com.metaro.portal.core.dto.position;

import br.com.metaro.portal.core.entities.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PositionMinDto {
    private Long id;
    private String name;

    public PositionMinDto(Position position) {
        id = position.getId();
        name = position.getName();
    }
}
