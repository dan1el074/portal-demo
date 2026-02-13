package br.com.metaro.portal.modules.general.internalCommunication.entities;

import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_interaction")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Interaction {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private InteractionPK id = new InteractionPK();

    @ManyToOne
    @MapsId("internalCommunicationId")
    @JoinColumn(name = "internal_communication_id")
    private InternalCommunication internalCommunication;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "position_signed_id")
    private Position departmentSigned;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    public Interaction(InternalCommunication ci, User user, Position departmentSigned) {
        this.internalCommunication = ci;
        this.user = user;
        this.departmentSigned = departmentSigned;
        this.createdAt = Instant.now();

        this.id = new InteractionPK(ci.getId(), user.getId());
    }
}
