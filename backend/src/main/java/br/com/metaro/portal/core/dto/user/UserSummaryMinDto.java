package br.com.metaro.portal.core.dto.user;

import br.com.metaro.portal.core.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserSummaryMinDto {
    Long id;
    String name;

    public UserSummaryMinDto(User user) {
        id = user.getId();
        name = user.getName();
    }
}
