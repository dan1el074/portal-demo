package br.com.metaro.portal.modules.general.rawMaterials.repositories;

import br.com.metaro.portal.modules.general.rawMaterials.repositories.projections.RawMaterialSummaryProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

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

    @Test
    void returnsOnlyProjectedPageFieldsFromSeed() {
        var page = repository.search(PageRequest.of(0, 10), "A36", "Chapas Finas", null, true);

        assertThat(page.getContent()).isNotEmpty().hasSizeLessThanOrEqualTo(10);
        assertThat(page.getContent().getFirst().getCode()).isNotBlank();
        assertThat(page.getContent().getFirst().getType()).isEqualTo("Chapas Finas");
    }

    @Test
    void summarizesActiveStockWithoutLoadingEntities() {
        RawMaterialSummaryProjection summary = repository.summarize();

        assertThat(summary.getLow() + summary.getOk() + summary.getHigh()).isEqualTo(82L);
    }
}
