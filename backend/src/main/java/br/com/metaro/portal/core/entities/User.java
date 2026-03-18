package br.com.metaro.portal.core.entities;

import br.com.metaro.portal.modules.general.memorando.entities.Interaction;
import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoLog;
import br.com.metaro.portal.modules.general.post.Post;
import br.com.metaro.portal.util.picture.Picture;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "tb_user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String email;
    private LocalDate birthDate;
    private Boolean activated;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updateAt;
    private String supportToken;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToMany
    @JoinTable(name = "tb_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    @OneToOne(mappedBy = "createdBy")
    private Notification notificationsCreated;

    @OneToMany(mappedBy = "user")
    private Set<MemorandoLog> ciLog = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "picture_id", unique = true)
    private Picture picture;

    @OneToMany(mappedBy = "createdBy")
    private Set<Memorando> memorandos = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Interaction> interactions = new HashSet<>();

    @ManyToMany(mappedBy = "manangers")
    private Set<Position> managedPositions = new HashSet<>();

    public List<Memorando> getInteractionsCI() {
        return interactions.stream().map(Interaction::getMemorando).toList();
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public boolean hasRole(String roleName) {
        for (Role role : roles) {
            if (role.getAuthority().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.activated;
    }
}
