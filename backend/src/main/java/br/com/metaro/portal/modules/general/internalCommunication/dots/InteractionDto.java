package br.com.metaro.portal.modules.general.internalCommunication.dots;

import br.com.metaro.portal.core.dto.PositionDto;
import br.com.metaro.portal.core.dto.UserSummaryDto;
import br.com.metaro.portal.modules.general.internalCommunication.entities.Interaction;
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
