package br.com.metaro.portal.modules.general.memorando.dots;

import br.com.metaro.portal.core.dto.position.PositionDto;
import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.modules.general.memorando.entities.Interaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InteractionDto {
    private UserSummaryDto user;
    private PositionDto departmentSigned;

    public InteractionDto (Interaction entity) {
        user = new UserSummaryDto(entity.getUser());
        departmentSigned = new PositionDto(entity.getDepartmentSigned());
    }
}
