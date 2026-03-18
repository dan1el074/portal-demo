package br.com.metaro.portal.core.dto.role;

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
    private String titleUrl;
    private String parent;
    private String parentUrl;

    public RoleDto(Role role) {
        id = role.getId();
        authority = role.getAuthority();
        title = role.getTitle();
        titleUrl = role.getTitleUrl();
        parent = role.getParent();
        parentUrl = role.getParentUrl();
    }
}
