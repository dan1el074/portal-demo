package br.com.metaro.portal.core.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_role")
@NoArgsConstructor
@Getter
@Setter
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String authority;
    private String title;
    private String titleUrl;
    private String parent;
    private String parentUrl;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    public Role(Long id, String authority, String title, String titleUrl, String parent, String parentUrl) {
        this.id = id;
        this.authority = authority;
        this.title = title;
        this.titleUrl = titleUrl;
        this.parent = parent;
        this.parentUrl = parentUrl;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
