package br.com.metaro.portal.core.entities;

import br.com.metaro.portal.modules.general.internalCommunication.InternalCommunication;
import jakarta.persistence.*;
import lombok.*;

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
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "position")
    private List<User> users = new ArrayList<>();

    @ManyToMany(mappedBy = "fromDepartments")
    private Set<InternalCommunication> InternalCommunications = new HashSet<>();
}
