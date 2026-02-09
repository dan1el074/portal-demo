package br.com.metaro.portal.modules.general.internalCommunication.entities;

import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tb_interaction")
public class Interaction {
    @EmbeddedId
    private InteractionPK id = new InteractionPK();
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "position_signed_id")
    private Position departmentSigned;

    public Interaction() {}

    public Interaction(InternalCommunication ci, User user, Position departmentSigned) {
        id.setInternalCommunication(ci);
        id.setUser(user);
        this.departmentSigned = departmentSigned;
        this.createdAt = Instant.now();
    }

    public InternalCommunication getCi() {
        return id.getInternalCommunication();
    }

    public User getUser() {
        return id.getUser();
    }

    public Position getDepartmentSigned() {
        return departmentSigned;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCi(InternalCommunication ci) {
        id.setInternalCommunication(ci);
    }

    public void setUser(User user) {
        id.setUser(user);
    }

    public void setDepartmentSigned(Position departmentSigned) {
        this.departmentSigned = departmentSigned;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
