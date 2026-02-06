package br.com.metaro.portal.core.dto;

import br.com.metaro.portal.core.entities.Param;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ParamDto {
    private Long id;
    private String name;
    private String content;

    public ParamDto(Param param) {
        id = param.getId();
        name = param.getName();
        content = param.getContent();
    }
}
