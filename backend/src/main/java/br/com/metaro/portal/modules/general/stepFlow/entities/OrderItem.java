package br.com.metaro.portal.modules.general.stepFlow.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_stepflow_order_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer itemCode;
    private String description;
    private String unit;
    private Integer quantity;
    private Integer producedQuantity;
    private Integer invoicedQuantity;
    private Double unitPrice;
    private Double total;

    // relacionamento
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
