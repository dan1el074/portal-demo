package br.com.metaro.portal.config.customgrant;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class CustomUserAuthorities {

    private Long id;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserAuthorities(Long id, String username, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}