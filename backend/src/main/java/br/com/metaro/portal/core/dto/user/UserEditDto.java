package br.com.metaro.portal.core.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserEditDto {
    private Long id;
    private Long pictureId;
    private String name;
    private Long positionId;
    private String email;
    private LocalDate birthDate;
    private String username;
    private List<Long> roles;
    private Boolean activated;
    private String supportToken;
}
