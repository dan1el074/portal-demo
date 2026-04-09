package br.com.metaro.portal.core.dto.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ErrorDto {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String path;
}
