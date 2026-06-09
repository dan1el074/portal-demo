package br.com.metaro.portal.util.picture;

import br.com.metaro.portal.modules.general.post.entities.Post;
import br.com.metaro.portal.modules.general.post.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class PictureService {
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private PostRepository postRepository;
    @Value("${app.server.image-path}")
    private String serverPath;

    @Transactional
    public List<Picture> saveFiles(List<MultipartFile> files, PictureType type, Post post) throws IOException {
        List<Picture> archives = new ArrayList<>();

        /// salva arquivo no servidor
        for (MultipartFile file : files) {
            String fileName = System.currentTimeMillis() + "_" + type.name() + ".jpg";
            Path filePath = Paths.get(serverPath, fileName);
            saveCompressedImage(file, filePath);

            Picture archive = new Picture();
            archive.setName(fileName);
            archive.setPath(filePath.toString());
            archive.setType(type);

            if (post != null) archive.setPost(post);

            archives.add(archive);
        }

        /// salva no banco
        return pictureRepository.saveAll(archives);
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

    private void saveCompressedImage(MultipartFile file, Path destination) throws IOException {
        BufferedImage original = ImageIO.read(file.getInputStream());

        if (original == null) throw new IOException("Arquivo não é uma imagem válida");

        final int maxWidth = 1920;

        BufferedImage imageToSave = original;

        if (original.getWidth() > maxWidth) {
            int newWidth = maxWidth;
            int newHeight = (original.getHeight() * newWidth) / original.getWidth();

            BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(original, 0, 0, newWidth, newHeight, null);
            g.dispose();

            imageToSave = resized;
        }

        if (imageToSave.getColorModel().hasAlpha()) {
            imageToSave = convertToRgb(imageToSave);
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

        if (!writers.hasNext()) {
            throw new IllegalStateException("Nenhum ImageWriter WebP encontrado. Verifique a dependência.");
        }

        ImageWriter writer = writers.next();

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(destination.toFile())) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();

            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.75f);
            }

            writer.write(null, new IIOImage(imageToSave, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    private BufferedImage convertToRgb(BufferedImage source) {
        BufferedImage rgbImage = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgbImage.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, source.getWidth(), source.getHeight());
        g.drawImage(source, 0, 0, null);
        g.dispose();

        return rgbImage;
    }
}
