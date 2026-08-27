package br.com.metaro.portal.modules.general.memorando.repository;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.UserRepository;
import br.com.metaro.portal.modules.general.memorando.entity.Memorando;
import br.com.metaro.portal.modules.general.memorando.entity.MemorandoStatus;
import br.com.metaro.portal.modules.general.memorando.repository.projections.MemorandoSummaryProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "external.datasource.jdbc-url=jdbc:h2:mem:memorando-repository-testdb",
        "external.datasource.driver-class-name=org.h2.Driver",
        "external.datasource.username=sa",
        "external.datasource.password="
})
class MemorandoRepositoryTests {
    @Autowired
    private MemorandoRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void paginatesSearchInTheDatabase() {
        User author = userRepository.findAll().getFirst();
        repository.saveAll(List.of(
                memorando("Cliente paginação um", "Primeiro", MemorandoStatus.PUBLISH, author),
                memorando("Cliente paginação dois", "Segundo", MemorandoStatus.PUBLISH, author),
                memorando("Cliente paginação três", "Terceiro", MemorandoStatus.APPROVED, author)
        ));

        var page = repository.search(
                PageRequest.of(0, 2), "paginação", false, null, true, author.getId()
        );

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    void extendedSearchFindsFieldsThatAreNotDisplayedInTheTable() {
        User author = userRepository.findAll().getFirst();
        Memorando saved = repository.save(memorando(
                "Cliente comum", "Ocorrência extraordinária no acabamento", MemorandoStatus.PUBLISH, author
        ));

        var regular = repository.search(
                PageRequest.of(0, 10), "extraordinária", false, null, true, author.getId()
        );
        var extended = repository.searchExtended(
                PageRequest.of(0, 10), "extraordinária", false, null, true, author.getId()
        );

        assertThat(regular.getContent()).isEmpty();
        assertThat(extended.getContent()).hasSize(1);

        var byAuthor = repository.searchExtended(
                PageRequest.of(0, 10), author.getName(), false, null, true, author.getId()
        );
        assertThat(byAuthor.getContent()).extracting(Memorando::getId).contains(saved.getId());

        var byItem = repository.searchExtended(
                PageRequest.of(0, 10), "ITEM-1", false, null, true, author.getId()
        );
        assertThat(byItem.getContent()).extracting(Memorando::getId).contains(saved.getId());
    }

    @Test
    void preservesDraftAndCanceledAccessRulesBeforePagination() {
        List<User> users = userRepository.findAll();
        User currentUser = users.getFirst();
        User anotherUser = users.get(1);
        repository.saveAll(List.of(
                memorando("Meu rascunho", "Rascunho próprio", MemorandoStatus.CREATED, currentUser),
                memorando("Outro rascunho", "Rascunho alheio", MemorandoStatus.CREATED, anotherUser),
                memorando("Cancelado alheio", "Cancelado alheio", MemorandoStatus.CANCELED, anotherUser)
        ));

        var drafts = repository.search(
                PageRequest.of(0, 10), "rascunho", true, null, false, currentUser.getId()
        );
        var canceled = repository.search(
                PageRequest.of(0, 10), "Cancelado alheio", false, MemorandoStatus.CANCELED,
                false, currentUser.getId()
        );

        assertThat(drafts.getContent()).hasSize(1);
        assertThat(drafts.getContent().getFirst().getCreatedBy().getId()).isEqualTo(currentUser.getId());
        assertThat(canceled.getContent()).isEmpty();
    }

    @Test
    void summarizesPublishedStatusesAndDraftsSeparately() {
        User author = userRepository.findAll().getFirst();
        MemorandoSummaryProjection before = repository.findSummary(true, author.getId());
        repository.saveAll(List.of(
                memorando("Ativo", "Ativo", MemorandoStatus.PUBLISH, author),
                memorando("Aprovado", "Aprovado", MemorandoStatus.APPROVED, author),
                memorando("Cancelado", "Cancelado", MemorandoStatus.CANCELED, author),
                memorando("Rascunho", "Rascunho", MemorandoStatus.CREATED, author)
        ));
        repository.flush();

        MemorandoSummaryProjection after = repository.findSummary(true, author.getId());

        assertThat(after.getTotal() - before.getTotal()).isEqualTo(3);
        assertThat(after.getActive() - before.getActive()).isEqualTo(1);
        assertThat(after.getApproved() - before.getApproved()).isEqualTo(1);
        assertThat(after.getCanceled() - before.getCanceled()).isEqualTo(1);
        assertThat(after.getDraft() - before.getDraft()).isEqualTo(1);
    }

    @Test
    void findsAccessiblePreviousAndNextMemorandosInsideTheSameGroup() {
        User author = userRepository.findAll().getFirst();
        List<Memorando> memorandos = repository.saveAll(List.of(
                memorando("Anterior", "Anterior", MemorandoStatus.PUBLISH, author),
                memorando("Atual", "Atual", MemorandoStatus.PUBLISH, author),
                memorando("Próximo", "Próximo", MemorandoStatus.APPROVED, author)
        ));
        repository.flush();

        Long currentId = memorandos.get(1).getId();
        List<Long> previous = repository.findPreviousAccessibleId(
                currentId, false, true, author.getId(), PageRequest.of(0, 1)
        );
        List<Long> next = repository.findNextAccessibleId(
                currentId, false, true, author.getId(), PageRequest.of(0, 1)
        );

        assertThat(previous).containsExactly(memorandos.getFirst().getId());
        assertThat(next).containsExactly(memorandos.getLast().getId());
    }

    private Memorando memorando(String client, String title, MemorandoStatus status, User author) {
        Memorando memorando = new Memorando();
        memorando.setRequest(12345L);
        memorando.setClient(client);
        memorando.setItems(new ArrayList<>(List.of("ITEM-1")));
        memorando.setTitle(title);
        memorando.setDescription("Descrição de teste");
        memorando.setReason("Motivo de teste");
        memorando.setStatus(status);
        memorando.setCreatedBy(author);
        memorando.setFromDepartments(new ArrayList<>());
        memorando.setLogs(new ArrayList<>());
        memorando.setSignatures(new ArrayList<>());
        if (status != MemorandoStatus.CREATED) {
            memorando.setNumber(900L + repository.count());
            memorando.setCreateAt(Instant.now());
        }
        return memorando;
    }
}
