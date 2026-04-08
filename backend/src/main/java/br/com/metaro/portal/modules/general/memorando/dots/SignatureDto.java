package br.com.metaro.portal.modules.general.memorando.dots;

import br.com.metaro.portal.core.dto.position.PositionDto;
import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.modules.general.memorando.entities.Signature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SignatureDto {
    private UserSummaryDto user;
    private PositionDto departmentSigned;
    private Boolean isSign;

    public SignatureDto(Signature entity) {
        user = new UserSummaryDto(entity.getUser());
        departmentSigned = new PositionDto(entity.getDepartmentSigned());
        isSign = entity.getIsSign();
    }
}
