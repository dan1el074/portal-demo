package br.com.metaro.portal.util.picture;

import br.com.metaro.portal.modules.general.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class PictureService {
    @Value("${app.server.image-path}")
    private String serverPath;

    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private PostRepository postRepository;

    @Transactional
    public List<Picture> saveFiles(List<MultipartFile> files, PictureType type) throws IOException {
        List<Picture> archives = new ArrayList<>();

        for (MultipartFile file : files) {
            // TODO: fazer uma compressão no arquivo se for muito grande.
            // TODO: a compressão precisa ter um perfil diferente para cada tipo:
            //      - fotos não precisam ser grandes, quadrado e tamanho fixo.
            //      - imagens de posts podem ser grandes em tamanho, mas não em armazenamento.

            /// salva arquivo no servidor
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(serverPath, fileName);
            Files.write(filePath, file.getBytes());

            Picture archive = new Picture();
            archive.setName(fileName);
            archive.setPath(filePath.toString());
            archive.setType(type);

            archives.add(archive);
        }

        /// salva no banco
        return pictureRepository.saveAll(archives);
    }

    @Transactional
    public void savePictures(List<Picture> pictures) {
        List<Picture> newPictures = new ArrayList<>();

        for (Picture entity : pictures) {
            Picture picture = pictureRepository.getReferenceById(entity.getId());
            picture.setName(entity.getName());
            picture.setPath(entity.getPath());
            picture.setType(entity.getType());
            picture.setPost(entity.getPost());
            newPictures.add(picture);
        }

        /// salva no banco
        pictureRepository.saveAll(newPictures);
    }

    @Transactional
    public void delete(Long id) throws IOException {
        if (!pictureRepository.existsById(id)) {
            throw new RuntimeException("Não foi possível localizar a imagem!");
        }
        Picture picture = pictureRepository.getReferenceById(id);
        Files.deleteIfExists(Paths.get(picture.getPath()));
        pictureRepository.deleteById(id);
    }
}
