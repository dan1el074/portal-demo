package br.com.metaro.portal.util.picture;

import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStep;
import br.com.metaro.portal.modules.general.stepFlow.entities.StepType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PictureServiceTests {

    @TempDir
    Path imageDirectory;

    private PictureService pictureService;

    @BeforeEach
    void setUp() {
        PictureRepository pictureRepository = mock(PictureRepository.class);
        when(pictureRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        pictureService = new PictureService();
        ReflectionTestUtils.setField(pictureService, "pictureRepository", pictureRepository);
        ReflectionTestUtils.setField(pictureService, "serverPath", imageDirectory.toString());
    }

    @Test
    void shouldUseOriginalNameAndGenerateUniqueServerNames() throws Exception {
        OrderStep step = new OrderStep();
        step.setStep(StepType.FINAL_ASSEMBLY);

        List<Picture> pictures = pictureService.saveStepFlowImages(
                List.of(
                        createImage("C:\\fakepath\\produto.png"),
                        createImage("produto.jpeg")
                ),
                step
        );

        assertThat(pictures)
                .extracting(Picture::getName)
                .containsExactly("EVIDENCIA_produto.jpg", "EVIDENCIA_produto.jpg");

        assertThat(pictures)
                .extracting(picture -> Path.of(picture.getPath()).getFileName().toString())
                .allMatch(name -> name.matches("STEP_FLOW\\d{15}[A-Z0-9]{3}\\.jpg"))
                .doesNotHaveDuplicates();

        assertThat(pictures)
                .allMatch(picture -> Path.of(picture.getPath()).toFile().isFile());
    }

    private MockMultipartFile createImage(String originalFilename) throws Exception {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", output);

        return new MockMultipartFile(
                "images",
                originalFilename,
                "image/jpeg",
                output.toByteArray()
        );
    }
}
