package br.com.metaro.portal.modules.general.rawMaterials.repositories;

import br.com.metaro.portal.modules.general.rawMaterials.entities.RawMaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RawMaterialCategoryRepository extends JpaRepository<RawMaterialCategory, Long> {
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("SELECT c.id FROM RawMaterialCategory c JOIN c.usersWithAccess u WHERE u.id = :userId")
    List<Long> findAllowedCategoryIds(@Param("userId") Long userId);
}
