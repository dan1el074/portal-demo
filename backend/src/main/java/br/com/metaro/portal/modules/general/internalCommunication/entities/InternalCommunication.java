package br.com.metaro.portal.modules.general.internalCommunication.entities;

import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tb_internal_communication")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class InternalCommunication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long number;
    private Long request;
    private String client;
    private String item;
    private String title;
    private String description;
    private String reason;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createAt;
    @Enumerated(EnumType.STRING)
    private InternalCommunicationStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    @ManyToMany
    @JoinTable(name = "tb_internal_communication_departments",
            joinColumns = @JoinColumn(name = "internal_communication_id"),
            inverseJoinColumns = @JoinColumn(name = "departments_id"))
    private List<Position> fromDepartments;

    @OneToMany(mappedBy = "internalCommunication")
    private List<InternalCommunicationLog> logs;

    @OneToMany(mappedBy = "id.internalCommunication")
    private Set<Interaction> interactions = new HashSet<>();

    public List<User> getInteractionsUsers() {
        return interactions.stream().map(Interaction::getUser).toList();
    }
}
