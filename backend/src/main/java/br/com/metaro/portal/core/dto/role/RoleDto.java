package br.com.metaro.portal.core.dto.role;

import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.repositories.projections.RoleProjection;
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
    private boolean activated;

    public RoleDto(Role role) {
        this.id = role.getId();
        this.authority = role.getAuthority();
        this.title = role.getTitle();
        this.titleUrl = role.getTitleUrl();
        this.parent = role.getParent();
        this.parentUrl = role.getParentUrl();
        this.activated = role.isActivated();
    }

    public RoleDto(RoleProjection projection) {
        this.id = projection.getId();
        this.authority = projection.getAuthority();
        this.title = projection.getTitle();
        this.titleUrl = projection.getTitleUrl();
        this.parent = projection.getParent();
        this.parentUrl = projection.getParentUrl();
        this.activated = projection.getActivated();
    }
}
