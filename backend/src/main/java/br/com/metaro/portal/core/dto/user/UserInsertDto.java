package br.com.metaro.portal.core.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInsertDto {
    private MultipartFile picture;
    @NotBlank(message = "Nome é obrigatório")
    private String name;
    @NotBlank(message = "Cargo é obrigatório")
    private String position;
    @Email(message = "Email inválido")
    @NotBlank
    private String email;
    @PastOrPresent(message = "Data inválida")
    @NotNull(message = "Data de nascimento é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    @NotBlank
    private String username;
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;
    private String roles;
    @NotBlank
    private String activated;
    private String supportToken;
}
