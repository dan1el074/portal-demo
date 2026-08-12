package br.com.metaro.portal.modules.general.rawMaterials.service;

import br.com.metaro.portal.core.repositories.*;
import br.com.metaro.portal.core.entities.*;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.rawMaterials.dto.RawMaterialInputDto;
import br.com.metaro.portal.modules.general.rawMaterials.dto.RawMaterialStockDto;
import br.com.metaro.portal.modules.general.rawMaterials.entities.*;
import br.com.metaro.portal.modules.general.rawMaterials.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RawMaterialsServiceValidationTests {
    @Mock RawMaterialRepository materialRepository;
    @Mock RawMaterialCategoryRepository categoryRepository;
    @Mock RawMaterialHistoryRepository historyRepository;
    @Mock RawMaterialCategoryDenialRepository denialRepository;
    @Mock UserService userService;
    @InjectMocks RawMaterialsService service;

    @Test
    void rejectsMaximumStockBelowMinimumBeforeWriting() {
        RawMaterialInputDto dto = validItem();
        dto.setMinStorage(BigDecimal.TEN);
        dto.setMaxStorage(BigDecimal.ONE);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining("máximo");
        verifyNoInteractions(userService, categoryRepository, historyRepository);
        verify(materialRepository, never()).save(any());
    }

    @Test
    void acceptsZeroLimitWithoutComparingStockRange() {
        RawMaterialInputDto dto = validItem();
        dto.setMinStorage(BigDecimal.TEN);
        dto.setMaxStorage(BigDecimal.ZERO);
        User administrator = new User();
        RawMaterialCategory category = new RawMaterialCategory();
        category.setId(1L);

        when(userService.authenticate()).thenReturn(administrator);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(materialRepository.save(any(RawMaterial.class))).thenAnswer(invocation -> {
            RawMaterial saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        service.create(dto);

        verify(materialRepository).save(any(RawMaterial.class));
    }

    @Test
    void rejectsDuplicatedCodeIgnoringCase() {
        RawMaterialInputDto dto = validItem();
        when(materialRepository.existsByCodeIgnoreCaseAndIdNot("ABC-01", 0L)).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining("código");
        verify(materialRepository, never()).save(any());
    }

    @Test
    void deniedCategoryCannotBeMovedEvenWhenOperatorAlsoHasConsultationRole() {
        User operator = new User();
        operator.setId(7L);
        Role operatorRole = mock(Role.class);
        Role consultationRole = mock(Role.class);
        when(operatorRole.getAuthority()).thenReturn("ROLE_RAW_MATERIALS_OPERATOR");
        when(consultationRole.getAuthority()).thenReturn("ROLE_RAW_MATERIALS_CONSULTATION");
        operator.setRoles(Set.of(operatorRole, consultationRole));

        RawMaterialCategory category = new RawMaterialCategory();
        category.setId(3L);
        RawMaterial item = new RawMaterial();
        item.setId(11L);
        item.setCategory(category);
        when(materialRepository.findById(11L)).thenReturn(Optional.of(item));
        when(userService.authenticate()).thenReturn(operator);
        when(denialRepository.findDeniedCategoryIds(7L)).thenReturn(List.of(3L));
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        assertThatThrownBy(() -> service.updateStock(11L, new RawMaterialStockDto(BigDecimal.TEN)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("movimentar");
        verify(materialRepository, never()).save(any());
    }

    @Test
    void recordsTheNamesOfFieldsChangedByAdministrator() {
        RawMaterialCategory category = new RawMaterialCategory();
        category.setId(1L);
        category.setName("Chapas Finas");
        RawMaterial item = material(category);
        User administrator = new User();
        administrator.setName("Administrador");

        RawMaterialInputDto dto = validItem();
        dto.setName("Chapa A36 galvanizada");
        dto.setMinStorage(new BigDecimal("2"));

        when(materialRepository.findById(11L)).thenReturn(Optional.of(item));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userService.authenticate()).thenReturn(administrator);

        service.update(11L, dto);

        ArgumentCaptor<RawMaterialHistory> history = ArgumentCaptor.forClass(RawMaterialHistory.class);
        verify(historyRepository).save(history.capture());
        assertThat(history.getValue().getAction()).isEqualTo("UPDATED");
        assertThat(history.getValue().getChangedFields())
                .isEqualTo("Descrição do item|Estoque mínimo");
    }

    private RawMaterial material(RawMaterialCategory category) {
        RawMaterial item = new RawMaterial();
        item.setId(11L);
        item.setCode("ABC-01");
        item.setName("Chapa A36");
        item.setDescription("");
        item.setCurrentStorage(BigDecimal.ONE);
        item.setMinStorage(BigDecimal.ONE);
        item.setMaxStorage(BigDecimal.TEN);
        item.setLength(BigDecimal.ONE);
        item.setWidth(BigDecimal.ONE);
        item.setThickness(BigDecimal.ONE);
        item.setWeightPerSquareMeter(BigDecimal.ONE);
        item.setActive(true);
        item.setCategory(category);
        return item;
    }

    private RawMaterialInputDto validItem() {
        return new RawMaterialInputDto("ABC-01", "Chapa A36", "", BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                1L, true);
    }
}
