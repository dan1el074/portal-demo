package br.com.metaro.portal.modules.general.memorando.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemorandoNavigationDto {
    private Long previousId;
    private Long nextId;
}
