package br.com.metaro.portal.util.picture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/images")
public class PictureController {
    @Value("${app.server.image-path}")
    private String serverPath;

    @Autowired
    private PictureRepository pictureRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable Long id) throws IOException {
        Picture picture = pictureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

        Path path = Paths.get(picture.getPath());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("Arquivo não encontrado no disco");
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.DAYS))
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
