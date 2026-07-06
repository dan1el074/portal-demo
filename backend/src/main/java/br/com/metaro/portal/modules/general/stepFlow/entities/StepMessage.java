package br.com.metaro.portal.modules.general.stepFlow.entities;

import br.com.metaro.portal.core.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_stepflow_step_message")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StepMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String message;

    // auditoria
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    // relacionamento
    @ManyToOne
    @JoinColumn(name = "step_id")
    private OrderStep step;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public StepMessage(String message, OrderStep step, User createdBy) {
        this.message = message;
        this.step = step;
        this.createdBy = createdBy;
    }

    // métodos
    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }
}
