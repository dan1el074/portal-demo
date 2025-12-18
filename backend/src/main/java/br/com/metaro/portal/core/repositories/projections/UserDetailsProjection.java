package br.com.metaro.portal.core.repositories.projections;

public interface UserDetailsProjection {
    String getUsername();
    String getPassword();
    Long getRoleId();
    String getAuthority();
    String getTitle();
    String getParent();
}
