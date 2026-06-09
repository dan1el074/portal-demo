package br.com.metaro.portal.core.dto.info;

import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.core.entities.User;
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

    public BirthdayDto(User user) {
        this.user = new UserSummaryDto(user);
        this.day = user.getBirthDate().getDayOfMonth();
        this.month = user.getBirthDate()
                .getMonth()
                .getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"))
                .replace(".", "");
    }
}
