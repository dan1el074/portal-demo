package br.com.metaro.portal.modules.general.memorando.entities;

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
public class Signature {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private SignaturePK id = new SignaturePK();
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @ManyToOne
    @MapsId("memorandoId")
    @JoinColumn(name = "memorando_id")
    private Memorando memorando;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "position_signed_id")
    private Position departmentSigned;

    public Signature(Memorando memorando, User user, Position departmentSigned) {
        this.memorando = memorando;
        this.user = user;
        this.departmentSigned = departmentSigned;
        this.createdAt = Instant.now();

        this.id = new SignaturePK(memorando.getId(), user.getId());
    }
}
