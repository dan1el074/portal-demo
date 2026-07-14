package br.com.metaro.portal.modules.general.stepFlow.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_stepflow_order")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer number;
    @Enumerated(EnumType.STRING)
    private StepType currentStep; // atualizar sempre que mexer no OrderStep
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // LATE apenas quando a dueDate passar (só no frontend)
    private Double shipment;
    private String carrier;

    // ERP
    private String client;
    private String cnpj;
    private String phone;
    private String seller;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String address;
    private Double subtotal;
    private Double discount;
    private Double total;

    // auditoria
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;

    // relacionamento
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OrderItem> items;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<OrderStep> steps;

    // métodos
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void addStep(OrderStep step) {
        steps.add(step);
        step.setOrder(this);
    }

    @Transient
    public List<Integer> getProgress() {
        Map<StepType, StepStatus> stepStatusMap = steps.stream()
                .collect(Collectors.toMap(OrderStep::getStep, OrderStep::getStatus));

        return Arrays.stream(StepType.values()).map(stepType -> {
            StepStatus stepStatus = stepStatusMap.getOrDefault(stepType, StepStatus.WAITING);
            return switch (stepStatus) {
                case WAITING -> 0;
                case ACTIVE -> 1;
                case DONE -> 2;
            };
        }).toList();
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
