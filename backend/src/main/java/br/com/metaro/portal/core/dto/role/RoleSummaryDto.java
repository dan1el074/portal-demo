package br.com.metaro.portal.core.dto.role;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class RoleSummaryDto {
    private Long id;
    private String authority;
    private boolean activated;
    private final List<RoleSummaryDto> childrens = new ArrayList<>();

    public RoleSummaryDto(Long id, String authority, boolean activated) {
        this.id = id;
        this.authority = authority;
        this.activated = activated;
    }
}
