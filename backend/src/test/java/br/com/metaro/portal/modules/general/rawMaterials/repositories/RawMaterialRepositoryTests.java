package br.com.metaro.portal.modules.general.rawMaterials.repositories;

import br.com.metaro.portal.modules.general.rawMaterials.repositories.projections.RawMaterialSummaryProjection;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "external.datasource.jdbc-url=jdbc:h2:mem:raw-material-repository-testdb",
        "external.datasource.driver-class-name=org.h2.Driver",
        "external.datasource.username=sa",
        "external.datasource.password="
})
class RawMaterialRepositoryTests {
    @Autowired
    private RawMaterialRepository repository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void returnsOnlyProjectedPageFieldsFromSeed() {
        var page = repository.search(PageRequest.of(0, 10), "A36", "Chapas Finas", null, true);

        assertThat(page.getContent()).isNotEmpty().hasSizeLessThanOrEqualTo(10);
        assertThat(page.getContent().getFirst().getCode()).isNotBlank();
        assertThat(page.getContent().getFirst().getType()).isEqualTo("Chapas Finas");
    }

    @Test
    void acceptsEmptyOptionalFilters() {
        var page = repository.search(PageRequest.of(0, 10), "", "", null, false);

        assertThat(page).isNotNull();
    }

    @Test
    @Transactional
    void summarizesActiveStockWithoutLoadingEntities() {
        // Isolate stock counts from development seed data; the transaction rolls back after the test.
        entityManager.createQuery("UPDATE RawMaterial r SET r.active = false").executeUpdate();
        insertStock("summary-low", 9, 10, 20, true);
        insertStock("summary-minimum", 10, 10, 20, true);
        insertStock("summary-middle", 15, 10, 20, true);
        insertStock("summary-maximum", 20, 10, 20, true);
        insertStock("summary-high", 21, 10, 20, true);
        insertStock("summary-no-minimum", 25, 0, 20, true);
        insertStock("summary-no-maximum", 5, 10, 0, true);
        insertStock("summary-inactive-low", 9, 10, 20, false);
        insertStock("summary-inactive-ok", 15, 10, 20, false);
        insertStock("summary-inactive-high", 21, 10, 20, false);

        RawMaterialSummaryProjection summary = repository.summarize();

        assertThat(summary.getLow()).isEqualTo(1L);
        assertThat(summary.getOk()).isEqualTo(5L);
        assertThat(summary.getHigh()).isEqualTo(1L);
    }

    private void insertStock(String code, int current, int minimum, int maximum, boolean active) {
        entityManager.createNativeQuery("""
                INSERT INTO tb_raw_material
                    (code, name, current_storage, min_storage, max_storage, active, category_id, updated_by)
                VALUES (:code, :code, :current, :minimum, :maximum, :active,
                    (SELECT MIN(id) FROM tb_raw_material_category), (SELECT MIN(id) FROM tb_user))
                """)
                .setParameter("code", code)
                .setParameter("current", current)
                .setParameter("minimum", minimum)
                .setParameter("maximum", maximum)
                .setParameter("active", active)
                .executeUpdate();
    }
}
