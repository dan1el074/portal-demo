package br.com.metaro.portal.modules.general.rawMaterials.repositories;

import br.com.metaro.portal.modules.general.rawMaterials.entities.RawMaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMaterialCategoryRepository extends JpaRepository<RawMaterialCategory, Long> {
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
