package br.com.metaro.portal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserInsertDto {
    private MultipartFile picture;
    private String name;
    private String position;
    private String email;
    private String birthDate;
    private String username;
    private String password;
    private String roles;
    private String activated;
}
