package br.com.metaro.portal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserInsertDto {
    private String name;
    private String position;
    private LocalDate birthDate;
    private String email;
    private String picture;
    private String username;
    private String password;
    private Boolean activated;
    private List<Long> roles;
}
