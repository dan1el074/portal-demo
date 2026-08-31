package br.com.metaro.portal.modules.general.memorando.dto;

import br.com.metaro.portal.core.dto.position.PositionDto;
import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.modules.general.memorando.entity.Signature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SignatureDto {
    private UserSummaryDto user;
    private PositionDto departmentSigned;
    private Boolean isSign;
    private Instant signedAt;

    public SignatureDto(Signature entity) {
        user = new UserSummaryDto(entity.getUser());
        departmentSigned = new PositionDto(entity.getDepartmentSigned());
        isSign = entity.getIsSign();
        signedAt = entity.getSignedAt();
    }
}
