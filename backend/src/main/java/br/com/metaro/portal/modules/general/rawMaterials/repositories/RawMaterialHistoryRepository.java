package br.com.metaro.portal.modules.general.rawMaterials.repositories;

import br.com.metaro.portal.modules.general.rawMaterials.entities.RawMaterialHistory;
import br.com.metaro.portal.modules.general.rawMaterials.repositories.projections.RawMaterialHistoryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface RawMaterialHistoryRepository extends JpaRepository<RawMaterialHistory, Long> {
    @Query("""
        SELECT h.id AS id, h.action AS action, h.previousStorage AS previousStorage,
               h.newStorage AS newStorage, h.changedFields AS changedFields,
               h.createdAt AS createdAt, u.name AS user
        FROM RawMaterialHistory h JOIN h.createdBy u
        WHERE h.rawMaterial.id = :itemId
    """)
    Page<RawMaterialHistoryProjection> findHistory(@Param("itemId") Long itemId, Pageable pageable);

    @Modifying
    @Query(value = """
        DELETE FROM tb_raw_material_history WHERE raw_material_id = :itemId AND id NOT IN (
            SELECT id FROM tb_raw_material_history WHERE raw_material_id = :itemId
            ORDER BY created_at DESC, id DESC LIMIT :limit
        )
    """, nativeQuery = true)
    void trim(@Param("itemId") Long itemId, @Param("limit") int limit);
}
