package br.com.metaro.portal.modules.general.memorando.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class SignaturePK implements Serializable {
    private Long memorandoId;
    private Long userId;
    private Long positionSignedId;
}
