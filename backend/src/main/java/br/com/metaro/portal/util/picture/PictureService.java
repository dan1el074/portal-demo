package br.com.metaro.portal.util.picture;

import br.com.metaro.portal.modules.general.post.entities.Post;
import br.com.metaro.portal.modules.general.post.repositories.PostRepository;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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
            String fileName = UUID.randomUUID() + "_" + type.name() + ".jpg";
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

        int orientation = getOrientation(file);
        original = correctOrientation(original, orientation);
        BufferedImage imageToSave = original;

        final int maxWidth = 1920;

        if (original.getWidth() > maxWidth) {
            int newWidth;
            int newHeight;

            if (original.getWidth() > original.getHeight()) {
                newWidth = maxWidth;
                newHeight = (original.getHeight() * newWidth) / original.getWidth();
            } else {
                newHeight = maxWidth;
                newWidth = (original.getWidth() * newHeight) / original.getHeight();
            }

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

    private BufferedImage correctOrientation(BufferedImage image, int orientation) {
        AffineTransform transform = new AffineTransform();

        switch (orientation) {
            case 6: // 90° CW
                transform.translate(image.getHeight(), 0);
                transform.rotate(Math.toRadians(90));
                break;

            case 3: // 180°
                transform.translate(image.getWidth(), image.getHeight());
                transform.rotate(Math.toRadians(180));
                break;

            case 8: // 270° CW
                transform.translate(0, image.getWidth());
                transform.rotate(Math.toRadians(270));
                break;

            default:
                return image;
        }

        BufferedImage rotated = new BufferedImage(
                orientation == 6 || orientation == 8 ? image.getHeight() : image.getWidth(),
                orientation == 6 || orientation == 8 ? image.getWidth() : image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = rotated.createGraphics();
        g2d.drawImage(image, transform, null);
        g2d.dispose();

        return rotated;
    }

    private int getOrientation(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            Metadata metadata = ImageMetadataReader.readMetadata(is);

            ExifIFD0Directory directory =
                    metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (directory != null &&
                    directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {

                return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }

        } catch (Exception ignored) {}

        return 1;
    }
}
