package br.com.metaro.portal.modules.general.stepFlow.entities;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.util.picture.Picture;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "tb_stepflow_order_step")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private StepType step;
    @Enumerated(EnumType.STRING)
    private StepStatus status;

    // auditoria
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant startedAt;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant finishedAt;

    // relacionamentos
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StepMessage> messages;
    @OneToMany(mappedBy = "orderStep", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Picture> pictures;
    @ManyToOne
    @JoinColumn(name = "finished_by_id", nullable = true)
    private User finishedBy;
}
