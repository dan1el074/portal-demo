package br.com.metaro.portal.util;

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
public class FileService {
    @Value("${app.server.image-path}")
    private String serverPath;

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private PostRepository postRepository;

    @Transactional
    public List<File> save(List<MultipartFile> files, FileType type) throws IOException {
        List<File> archives = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(serverPath, fileName);
            Files.write(filePath, file.getBytes());

            File archive = new File();
            archive.setName(fileName);
            archive.setPath(filePath.toString());
            archive.setType(type);

            archives.add(archive);
        }

        return fileRepository.saveAll(archives);
    }

    @Transactional
    public void delete(Long id) {
        if (!fileRepository.existsById(id)) {
            throw new RuntimeException("Não foi possível localizar a imagem!");
        }
        fileRepository.deleteById(id);
    }

    @Transactional
    public List<File> setPost(List<File> files) {
        List<File> newFiles = new ArrayList<>();

        for (File entity : files) {
            File file = fileRepository.getReferenceById(entity.getId());
            file.setName(entity.getName());
            file.setPath(entity.getPath());
            file.setType(entity.getType());
            file.setPost(entity.getPost());
            newFiles.add(file);
        }

        return fileRepository.saveAll(newFiles);
    }
}
