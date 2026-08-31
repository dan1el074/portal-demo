package br.com.metaro.portal.modules.general.memorando.service;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.modules.general.memorando.repository.MemorandoRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemorandoServiceTests {
    @Mock
    private MemorandoRepository memorandoRepository;
    @Mock
    private UserService userService;
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
}
