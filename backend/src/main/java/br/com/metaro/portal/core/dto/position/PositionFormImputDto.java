package br.com.metaro.portal.core.dto.position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PositionFormImputDto {
    String name;
    List<Long> manangers;
    Boolean activated;
}
