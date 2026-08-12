package br.com.metaro.portal.modules.general.rawMaterials.entities;

import br.com.metaro.portal.core.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_raw_material_category_denial",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialCategoryDenial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private RawMaterialCategory category;
}
