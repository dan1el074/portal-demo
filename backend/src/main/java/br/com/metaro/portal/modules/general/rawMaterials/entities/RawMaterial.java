package br.com.metaro.portal.modules.general.rawMaterials.entities;

import br.com.metaro.portal.core.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tb_raw_material")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal currentStorage = BigDecimal.ZERO;
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal minStorage = BigDecimal.ZERO;
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal maxStorage = BigDecimal.ZERO;
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal length = BigDecimal.ZERO;
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal width = BigDecimal.ZERO;
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal thickness = BigDecimal.ZERO;
    @Column(nullable = false, precision = 18, scale = 3, columnDefinition = "numeric(18,3) default 0")
    private BigDecimal height = BigDecimal.ZERO;
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal weightPerSquareMeter = BigDecimal.ZERO;
    @Column(nullable = false, precision = 18, scale = 3, columnDefinition = "numeric(18,3) default 0")
    private BigDecimal litersPerUnit = BigDecimal.ZERO;
    @Column(nullable = false, precision = 18, scale = 3, columnDefinition = "numeric(18,3) default 0")
    private BigDecimal weightPerLinearMeter = BigDecimal.ZERO;
    @Column(nullable = false)
    private Boolean active = true;
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private RawMaterialCategory category;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
