package br.com.metaro.portal.modules.general.memorando.dots;

import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemorandoInsertDto {
    private Long request;
    private String client;
    private List<String> items;
    private String title;
    private String description;
    private String reason;
    private MemorandoStatus status;
    private List<Long> departments;
}
