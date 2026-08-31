package br.com.metaro.portal.modules.general.memorando.service;

import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.modules.general.memorando.entity.Memorando;
import br.com.metaro.portal.modules.general.memorando.entity.MemorandoStatus;
import br.com.metaro.portal.modules.general.memorando.repository.MemorandoRepository;
import br.com.metaro.portal.modules.general.memorando.util.MemorandoUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemorandoServiceTests {
    @Mock
    private MemorandoRepository memorandoRepository;
    @Mock
    private UserService userService;
    @Mock
    private MemorandoUtil memorandoUtil;
    @Mock
    private MemorandoLogService memorandoLogService;
    @InjectMocks
    private MemorandoService service;

    @Test
    void removesPageableSortFromPostgresFullTextSearch() {
        User user = new User();
        user.setId(42L);
        when(userService.authenticate()).thenReturn(user);
        when(memorandoRepository.searchFullText(
                any(Pageable.class), anyString(), anyBoolean(), anyString(), anyBoolean(), anyLong()
        )).thenReturn(new PageImpl<>(List.of()));
        ReflectionTestUtils.setField(service, "datasourceDriver", "org.postgresql.Driver");

        service.listMemorandos(
                PageRequest.of(2, 25, Sort.by(Sort.Order.desc("createAt"), Sort.Order.desc("id"))),
                "PUBLISHED",
                null,
                "MT-101513",
                true
        );

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(memorandoRepository).searchFullText(
                pageableCaptor.capture(),
                anyString(),
                anyBoolean(),
                anyString(),
                anyBoolean(),
                anyLong()
        );
        Pageable fullTextPageable = pageableCaptor.getValue();
        assertThat(fullTextPageable.getPageNumber()).isEqualTo(2);
        assertThat(fullTextPageable.getPageSize()).isEqualTo(25);
        assertThat(fullTextPageable.getSort().isUnsorted()).isTrue();
    }

    @Test
    void allowsAnAdministratorToCancelAnApprovedMemorando() {
        User admin = userWithRole("ROLE_ADMIN");
        Memorando memorando = approvedMemorando(admin);
        when(userService.authenticate()).thenReturn(admin);
        when(memorandoRepository.findById(memorando.getId())).thenReturn(Optional.of(memorando));

        service.cancelMemorando(memorando.getId());

        assertThat(memorando.getStatus()).isEqualTo(MemorandoStatus.CANCELED);
        verify(memorandoRepository).save(memorando);
        verify(memorandoUtil).removeNotifications(memorando);
        verify(memorandoLogService).create(memorando.getId(), "Cancelou o documento nº 101/2026");
    }

    @Test
    void preventsANonAdministratorFromCancelingAnApprovedMemorando() {
        User user = userWithRole("ROLE_MEMORANDO");
        Memorando memorando = approvedMemorando(user);
        when(userService.authenticate()).thenReturn(user);
        when(memorandoRepository.findById(memorando.getId())).thenReturn(Optional.of(memorando));

        assertThatThrownBy(() -> service.cancelMemorando(memorando.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Apenas administradores podem cancelar um Memorando!");

        assertThat(memorando.getStatus()).isEqualTo(MemorandoStatus.APPROVED);
        verify(memorandoRepository, never()).save(memorando);
    }

    private User userWithRole(String authority) {
        Role role = new Role();
        role.setAuthority(authority);
        Position position = new Position();
        position.setId(1L);
        position.setName("Gestão");
        User user = new User();
        user.setId(42L);
        user.setName("Usuário de teste");
        user.setPosition(position);
        user.addRole(role);
        return user;
    }

    private Memorando approvedMemorando(User author) {
        Memorando memorando = new Memorando();
        memorando.setId(10L);
        memorando.setNumber(101L);
        memorando.setRequest(12345L);
        memorando.setClient("Cliente");
        memorando.setItems(new ArrayList<>());
        memorando.setTitle("Título");
        memorando.setDescription("Descrição");
        memorando.setReason("Motivo");
        memorando.setCreateAt(Instant.parse("2026-08-31T12:00:00Z"));
        memorando.setCreatedBy(author);
        memorando.setFromDepartments(new ArrayList<>());
        memorando.setSignatures(new ArrayList<>());
        memorando.setLogs(new ArrayList<>());
        memorando.setStatus(MemorandoStatus.APPROVED);
        return memorando;
    }
}
