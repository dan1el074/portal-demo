package br.com.metaro.portal.core.entities;

import br.com.metaro.portal.modules.general.internalCommunication.entities.Interaction;
import br.com.metaro.portal.modules.general.internalCommunication.entities.InternalCommunication;
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
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "mananger_id")
    private User mananger;

    @OneToMany(mappedBy = "position")
    private List<User> users = new ArrayList<>();

    @ManyToMany(mappedBy = "fromDepartments")
    private Set<InternalCommunication> ciTarget = new HashSet<>();

    @OneToMany(mappedBy = "departmentSigned")
    private List<Interaction> ciInteractions = new ArrayList<>();
}
