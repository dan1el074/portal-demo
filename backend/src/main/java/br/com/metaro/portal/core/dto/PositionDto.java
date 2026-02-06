package br.com.metaro.portal.core.dto;

import br.com.metaro.portal.core.entities.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PositionDto {
    private Long id;
    private String name;

    public PositionDto(Position position) {
        id = position.getId();
        name = position.getName();
    }
}
