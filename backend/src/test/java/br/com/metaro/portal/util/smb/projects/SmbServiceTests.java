package br.com.metaro.portal.util.smb.projects;

import br.com.metaro.portal.util.smb.projects.dto.ProjectSearchResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.share.DiskShare;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SmbServiceTests {
    @Test
    void serializesDtoWithEnglishSourceAndFileName() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        var json = mapper.readTree(mapper.writeValueAsString(new ProjectSearchResultDto("60390.pdf", ProjectSource.NEW)));
        assertThat(json.get("fileName").asText()).isEqualTo("60390.pdf");
        assertThat(json.get("source").asText()).isEqualTo("NEW");
    }

    @ParameterizedTest
    @ValueSource(strings = {"60390.pdf", "49852-ADQ.pdf", "49856-DET1.pdf", "49856-DET2.pdf", "49964_FABR1.pdf", "60390.PDF"})
    void includesCurrentProjectsAndDrawingVariants(String name) {
        assertThat(SmbService.matchesProject(name, name.substring(0, 5), ProjectSource.NEW)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"60390_08.09.26_09.33.pdf", "60390_04.09.26_10.01.pdf", "60390_FABR1_08.09.2026_09.33.PDF"})
    void excludesDatedVersionsOnlyFromNewShare(String name) {
        assertThat(SmbService.matchesProject(name, "60390", ProjectSource.NEW)).isFalse();
        assertThat(SmbService.matchesProject(name, "60390", ProjectSource.OLD)).isTrue();
    }

    @Test
    void combinesBothSharesWithoutDeduplicatingNames() throws Exception {
        SmbService service = spy(new SmbService());
        DiskShare recent = mock(DiskShare.class);
        DiskShare legacy = mock(DiskShare.class);
        doReturn(recent).when(service).getShare(ProjectSource.NEW);
        doReturn(legacy).when(service).getShare(ProjectSource.OLD);
        when(legacy.folderExists("60000")).thenReturn(true);
        doReturn(List.of(entry("60390.pdf"), entry("60390_08.09.26_09.33.pdf"), entry("60391.pdf"))).when(recent).list("");
        doReturn(List.of(entry("60390.pdf"), entry("60390_04.09.26_10.01.pdf"))).when(legacy).list("60000");

        assertThat(service.searchProject(" 60390 ")).containsExactly(
                new ProjectSearchResultDto("60390.pdf", ProjectSource.NEW),
                new ProjectSearchResultDto("60390.pdf", ProjectSource.OLD),
                new ProjectSearchResultDto("60390_04.09.26_10.01.pdf", ProjectSource.OLD));
    }

    @Test
    void missingLegacyFolderDoesNotHideNewProjects() throws Exception {
        SmbService service = spy(new SmbService());
        DiskShare recent = mock(DiskShare.class);
        DiskShare legacy = mock(DiskShare.class);
        doReturn(recent).when(service).getShare(ProjectSource.NEW);
        doReturn(legacy).when(service).getShare(ProjectSource.OLD);
        doReturn(List.of(entry("60390.pdf"))).when(recent).list("");
        assertThat(service.searchProject("60390")).containsExactly(new ProjectSearchResultDto("60390.pdf", ProjectSource.NEW));
        verify(legacy, never()).list(anyString());
    }

    @Test
    void opensPdfUsingItsSourcePath() throws Exception {
        SmbService service = spy(new SmbService());
        DiskShare recent = mock(DiskShare.class);
        DiskShare legacy = mock(DiskShare.class);
        doReturn(recent).when(service).getShare(ProjectSource.NEW);
        doReturn(legacy).when(service).getShare(ProjectSource.OLD);
        service.getProjectPdfStream("60390.pdf", ProjectSource.NEW);
        service.getProjectPdfStream("60390.pdf", ProjectSource.OLD);
        verify(recent).fileExists("60390.pdf");
        verify(legacy).fileExists("60000/60390.pdf");
    }

    private FileIdBothDirectoryInformation entry(String name) {
        FileIdBothDirectoryInformation entry = mock(FileIdBothDirectoryInformation.class);
        when(entry.getFileName()).thenReturn(name);
        return entry;
    }
}
