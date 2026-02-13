package br.com.metaro.portal.modules.general.internalCommunication.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class InteractionPK implements Serializable {

    private Long internalCommunicationId;
    private Long userId;
}
