package br.com.metaro.portal.core.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Objects;
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
    private String parent;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    public Role(Long id, String authority, String title, String parent) {
        this.id = id;
        this.authority = authority;
        this.title = title;
        this.parent = parent;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(authority, role.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authority);
    }
}
