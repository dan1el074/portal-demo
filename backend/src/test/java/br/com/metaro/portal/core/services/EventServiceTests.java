package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.entities.Event;
import br.com.metaro.portal.core.repositories.EventRepository;
import br.com.metaro.portal.util.picture.Picture;
import br.com.metaro.portal.util.picture.PictureRepository;
import br.com.metaro.portal.util.picture.PictureType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "external.datasource.jdbc-url=jdbc:h2:mem:event-service-testdb",
        "external.datasource.driver-class-name=org.h2.Driver",
        "external.datasource.username=sa",
        "external.datasource.password="
})
class EventServiceTests {
    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void deletesEventAndItsPictureWithoutTransientReferenceError(@TempDir Path tempDirectory) throws Exception {
        Path imagePath = Files.createFile(tempDirectory.resolve("event.jpg"));

        Picture picture = new Picture();
        picture.setName("event.jpg");
        picture.setPath(imagePath.toString());
        picture.setType(PictureType.POST);
        picture = pictureRepository.saveAndFlush(picture);

        Event event = new Event();
        event.setTitle("Evento de teste");
        event.setEventDate(Instant.now().plusSeconds(3600));
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());
        event.setPicture(picture);
        event = eventRepository.saveAndFlush(event);

        Long eventId = event.getId();
        Long pictureId = picture.getId();
        entityManager.clear();

        eventService.delete(eventId);

        assertThat(eventRepository.findById(eventId)).isEmpty();
        assertThat(pictureRepository.findById(pictureId)).isEmpty();
        assertThat(imagePath).doesNotExist();
    }
}
