package br.com.metaro.portal.core.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserConfigInsertDto {
    private MultipartFile picture;
    private String name;
    private String email;
    private String birthDate;
    private String password;
}
