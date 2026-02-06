package br.com.metaro.portal.modules.general.internalControl;

import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_internal_control")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class InternalControl {
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
    private InternalControlStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "tb_internal_control_departments",
            joinColumns = @JoinColumn(name = "internal_control_id"),
            inverseJoinColumns = @JoinColumn(name = "departments_id"))
    private List<Position> fromDepartments;

    @ManyToMany
    @JoinTable(name = "tb_internal_control_interaction",
            joinColumns = @JoinColumn(name = "internal_control_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> interactions;

    // TODO: criar uma entidade para os logs, e fazer um @OneToMany para lá
    //          ...para cada alteração, vamos criar um log documentando tudo o que rolou!
}
