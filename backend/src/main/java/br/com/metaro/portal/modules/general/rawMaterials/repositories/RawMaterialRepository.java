package br.com.metaro.portal.modules.general.rawMaterials.repositories;

import br.com.metaro.portal.modules.general.rawMaterials.entities.RawMaterial;
import br.com.metaro.portal.modules.general.rawMaterials.repositories.projections.RawMaterialListProjection;
import br.com.metaro.portal.modules.general.rawMaterials.repositories.projections.RawMaterialSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
    long countByCategoryId(Long categoryId);

    @Query("""
        SELECT r.id AS id, r.code AS code, r.name AS name, r.description AS description,
               r.currentStorage AS currentStorage, r.minStorage AS minStorage, r.maxStorage AS maxStorage,
               r.length AS length, r.width AS width, r.thickness AS thickness,
               r.weightPerSquareMeter AS weightPerSquareMeter, c.conversionFactor AS conversionFactor,
               c.name AS type, r.active AS active,
               r.updatedAt AS updateAt, u.name AS user
        FROM RawMaterial r JOIN r.category c JOIN r.updatedBy u
        WHERE r.active = :active
          AND (:category IS NULL OR LOWER(c.name) = LOWER(:category))
          AND (:search = '' OR LOWER(r.code) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(COALESCE(r.description, '')) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL
               OR (:status = 'low' AND r.minStorage > 0 AND r.maxStorage > 0 AND r.currentStorage < r.minStorage)
               OR (:status = 'high' AND r.minStorage > 0 AND r.maxStorage > 0 AND r.currentStorage > r.maxStorage)
               OR (:status = 'ok' AND (r.minStorage = 0 OR r.maxStorage = 0 OR r.currentStorage BETWEEN r.minStorage AND r.maxStorage)))
    """)
    Page<RawMaterialListProjection> search(Pageable pageable, @Param("search") String search,
        @Param("category") String category, @Param("status") String status, @Param("active") boolean active);

    @Query("""
        SELECT r.id AS id, r.code AS code, r.name AS name, r.description AS description,
               r.currentStorage AS currentStorage, r.minStorage AS minStorage, r.maxStorage AS maxStorage,
               r.length AS length, r.width AS width, r.thickness AS thickness,
               r.weightPerSquareMeter AS weightPerSquareMeter, c.conversionFactor AS conversionFactor,
               c.name AS type, r.active AS active,
               r.updatedAt AS updateAt, u.name AS user
        FROM RawMaterial r JOIN r.category c JOIN r.updatedBy u
        WHERE r.active = :active AND c.id IN :categoryIds
          AND (:category IS NULL OR LOWER(c.name) = LOWER(:category))
          AND (:search = '' OR LOWER(r.code) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL
               OR (:status = 'low' AND r.minStorage > 0 AND r.maxStorage > 0 AND r.currentStorage < r.minStorage)
               OR (:status = 'high' AND r.minStorage > 0 AND r.maxStorage > 0 AND r.currentStorage > r.maxStorage)
               OR (:status = 'ok' AND (r.minStorage = 0 OR r.maxStorage = 0 OR r.currentStorage BETWEEN r.minStorage AND r.maxStorage)))
    """)
    Page<RawMaterialListProjection> searchAllowed(Pageable pageable, @Param("search") String search,
        @Param("category") String category, @Param("status") String status, @Param("active") boolean active,
        @Param("categoryIds") Collection<Long> categoryIds);

    @Query("""
        SELECT COUNT(CASE WHEN r.minStorage > 0 AND r.maxStorage > 0 AND r.currentStorage < r.minStorage THEN 1 END) AS low,
               COUNT(CASE WHEN r.minStorage = 0 OR r.maxStorage = 0 OR r.currentStorage BETWEEN r.minStorage AND r.maxStorage THEN 1 END) AS ok,
               COUNT(CASE WHEN r.minStorage > 0 AND r.maxStorage > 0 AND r.currentStorage > r.maxStorage THEN 1 END) AS high
        FROM RawMaterial r WHERE r.active = true
    """)
    RawMaterialSummaryProjection summarize();
}
