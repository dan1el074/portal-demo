package br.com.metaro.portal.util.smb;

import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
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
    public ResponseEntity<List<String>> search(@RequestParam String term) {
        List<String> files = smbService.searchProject(term);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<StreamingResponseBody> getPdf(@PathVariable String fileName) throws Exception {
        SmbFileStream smbFile = smbService.getProjectPdfStream(fileName);

        if (smbFile == null) throw new ResourceNotFoundException();

        StreamingResponseBody stream = outputStream -> {
            try (InputStream in = smbFile.inputStream()) {
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
