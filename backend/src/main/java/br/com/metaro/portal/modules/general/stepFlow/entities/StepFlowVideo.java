package br.com.metaro.portal.modules.general.stepFlow.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_stepflow_video")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StepFlowVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false, unique = true)
    private String bunnyVideoId;
    private String viewUrl;
    @Enumerated(EnumType.STRING)
    private VideoStatus status;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;
    @ManyToOne
    @JoinColumn(name = "order_step_id")
    private OrderStep orderStep;
    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        if (status == null) status = VideoStatus.PENDING;
    }
}
