package br.com.metaro.portal.modules.general.rawMaterials.repositories;

import br.com.metaro.portal.modules.general.rawMaterials.entities.RawMaterialCategoryDenial;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RawMaterialCategoryDenialRepository extends JpaRepository<RawMaterialCategoryDenial, Long> {
    @Query("SELECT d.category.id FROM RawMaterialCategoryDenial d WHERE d.user.id = :userId")
    List<Long> findDeniedCategoryIds(@Param("userId") Long userId);

    void deleteByUserId(Long userId);
}
