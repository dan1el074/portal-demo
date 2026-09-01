package br.com.metaro.portal.integration.bunny;

import br.com.metaro.portal.core.entities.Param;
import br.com.metaro.portal.core.repositories.ParamRepository;
import br.com.metaro.portal.integration.bunny.dto.BunnyConfigDto;
import br.com.metaro.portal.integration.bunny.dto.BunnyConfigUpdateDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BunnyConfigServiceTests {
    @Test
    void returnsAnEmptyConfigurationWithoutCreatingAutomaticParameters() {
        ParamRepository repository = mock(ParamRepository.class);
        when(repository.findByName(org.mockito.ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        BunnyConfigService service = new BunnyConfigService(repository);

        BunnyConfigDto config = service.getConfig();

        assertThat(config.getLibraryId()).isEmpty();
        assertThat(config.isApiKeyConfigured()).isFalse();
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void preservesTheCurrentApiKeyWhenTheUpdateLeavesItBlank() {
        ParamRepository repository = mock(ParamRepository.class);
        Param currentApiKey = new Param(1L, BunnyConfigService.API_KEY_PARAM, "current-secret");
        when(repository.findByName(BunnyConfigService.API_KEY_PARAM)).thenReturn(Optional.of(currentApiKey));
        when(repository.findByName(BunnyConfigService.LIBRARY_ID_PARAM)).thenReturn(Optional.empty());
        BunnyConfigService service = new BunnyConfigService(repository);
        BunnyConfigUpdateDto update = new BunnyConfigUpdateDto();
        update.setLibraryId(" test-library ");
        update.setApiKey(" ");

        BunnyConfigDto config = service.updateConfig(update);

        assertThat(config.getLibraryId()).isEqualTo("test-library");
        assertThat(config.isApiKeyConfigured()).isTrue();
        ArgumentCaptor<Param> savedParam = ArgumentCaptor.forClass(Param.class);
        verify(repository, org.mockito.Mockito.times(2)).save(savedParam.capture());
        assertThat(savedParam.getAllValues())
                .filteredOn(param -> BunnyConfigService.API_KEY_PARAM.equals(param.getName()))
                .singleElement()
                .extracting(Param::getContent)
                .isEqualTo("current-secret");
    }
}
