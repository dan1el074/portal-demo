package br.com.metaro.portal.modules.general.rawMaterials.entities;

import br.com.metaro.portal.core.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tb_raw_material_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String action;
    @Column(precision = 18, scale = 3)
    private BigDecimal previousStorage;
    @Column(precision = 18, scale = 3)
    private BigDecimal newStorage;
    @Column(length = 1000)
    private String changedFields;
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}
