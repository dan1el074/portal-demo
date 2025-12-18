package br.com.metaro.portal.core.dto;

import br.com.metaro.portal.core.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleDto {
    private Long id;
    private String authority;
    private String title;
    private String parent;

    public RoleDto(Role role) {
        this.id = role.getId();
        this.authority = role.getAuthority();
        this.title = role.getTitle();
        this.parent = role.getParent();
    }
}
