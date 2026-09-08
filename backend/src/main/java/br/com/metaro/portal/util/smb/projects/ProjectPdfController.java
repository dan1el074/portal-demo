package br.com.metaro.portal.util.smb.projects;

import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.util.smb.projects.dto.SmbFileStreamDto;
import br.com.metaro.portal.util.smb.projects.dto.ProjectSearchResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/pdf")
public class ProjectPdfController {
    @Autowired
    private SmbService smbService;

    @GetMapping("/search")
    public ResponseEntity<List<ProjectSearchResultDto>> search(@RequestParam String term) {
        List<ProjectSearchResultDto> files = smbService.searchProject(term);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<StreamingResponseBody> getPdf(@PathVariable String fileName,
            @RequestParam(defaultValue = "OLD") ProjectSource source) {
        SmbFileStreamDto smbFile = smbService.getProjectPdfStream(fileName, source);

        if (smbFile == null) throw new ResourceNotFoundException();

        StreamingResponseBody stream = outputStream -> {
            try (InputStream in = smbFile.getInputStream()) {
                in.transferTo(outputStream);
            } finally {
                smbFile.close();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(stream);
    }
}
