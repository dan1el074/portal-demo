package br.com.metaro.portal.modules.general.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostInsertDto {
    private String text;
    private MultipartFile[] images;
    private String isWarning;
}
