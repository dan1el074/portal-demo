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
@Table(name = "tb_signature")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Signature {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private SignaturePK id = new SignaturePK();
    private Boolean isSign;
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
    @MapsId("positionSignedId")
    @JoinColumn(name = "position_signed_id")
    private Position departmentSigned;

    public Signature(Memorando memorando, Boolean isSign, User user, Position departmentSigned) {
        this.memorando = memorando;
        this.isSign = isSign;
        this.user = user;
        this.departmentSigned = departmentSigned;
        this.createdAt = Instant.now();

        this.id = new SignaturePK(memorando.getId(), user.getId(), departmentSigned.getId());
    }
}
