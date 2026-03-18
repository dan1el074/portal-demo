package br.com.metaro.portal.core.dto.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleSummaryDto {
    private Long id;
    private String authority;
}
