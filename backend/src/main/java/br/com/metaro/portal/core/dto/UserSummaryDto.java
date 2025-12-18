package br.com.metaro.portal.core.dto;

import br.com.metaro.portal.core.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserSummaryDto {
    private Long id;
    private String name;

    public UserSummaryDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }
}
