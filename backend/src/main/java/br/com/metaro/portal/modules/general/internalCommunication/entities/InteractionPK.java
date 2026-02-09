package br.com.metaro.portal.modules.general.internalCommunication.entities;

import br.com.metaro.portal.core.entities.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InteractionPK {
    @ManyToOne
    @JoinColumn(name = "internal_communication_id")
    private InternalCommunication internalCommunication;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
