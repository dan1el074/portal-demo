package br.com.metaro.portal.core.repositories.projections;

public interface RoleProjection {
    Long getId();
    String getAuthority();
    String getTitle();
    String getTitleUrl();
    String getParent();
    String getParentUrl();
}
