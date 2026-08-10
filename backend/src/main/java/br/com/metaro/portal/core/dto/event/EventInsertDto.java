package br.com.metaro.portal.core.dto.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
public class EventInsertDto {
    private String title;
    private Instant eventDate;
    private MultipartFile image;
}
