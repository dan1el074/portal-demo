package br.com.metaro.portal.util.smb.files;

import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.util.smb.projects.SmbFileStream;
import br.com.metaro.portal.util.smb.projects.SmbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

@RestController
@RequestMapping("/api/file")
public class FileController {
    @Autowired
    private SmbService smbService;

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<StreamingResponseBody> getMedia(@PathVariable String fileName) throws Exception {
        SmbFileStream smbFile = smbService.getFileStream(fileName);

        if (smbFile == null) throw new ResourceNotFoundException();

        MediaType mediaType = smbService.resolveMediaType(fileName);

        StreamingResponseBody stream = outputStream -> {
            try (InputStream in = smbFile.inputStream()) {
                in.transferTo(outputStream);
            } finally {
                smbFile.close();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(mediaType)
                .body(stream);
    }
}
