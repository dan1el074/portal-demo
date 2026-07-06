package br.com.metaro.portal.core.dto.info;

import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.core.repositories.projections.BirthdayProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.TextStyle;
import java.util.Locale;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BirthdayDto {
    private UserSummaryDto user;
    private Integer day;
    private String month;

    public BirthdayDto(BirthdayProjection projection) {
        this.user = new UserSummaryDto(
                projection.getId(),
                projection.getName(),
                projection.getPictureId(),
                projection.getPositionName()
        );
        this.day = projection.getBirthDate().getDayOfMonth();
        this.month = projection.getBirthDate()
                .getMonth()
                .getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"))
                .replace(".", "");
    }
}
