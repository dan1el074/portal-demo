package br.com.metaro.portal.core.entities;

import br.com.metaro.portal.modules.general.memorando.entities.Signature;
import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tb_position")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private Boolean activated;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @ManyToMany
    @JoinTable(name = "tb_position_manangers",
            joinColumns = @JoinColumn(name = "position_id"),
            inverseJoinColumns = @JoinColumn(name = "mananger_id"))
    private Set<User> manangers = new HashSet<>();

    @OneToMany(mappedBy = "position")
    private List<User> users = new ArrayList<>();

    @ManyToMany(mappedBy = "fromDepartments")
    private Set<Memorando> memorandoTarget = new HashSet<>();

    @OneToMany(mappedBy = "departmentSigned")
    private List<Signature> memorandoSignatures = new ArrayList<>();
}
