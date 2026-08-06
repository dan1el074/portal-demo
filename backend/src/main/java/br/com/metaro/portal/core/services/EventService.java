package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.event.EventDto;
import br.com.metaro.portal.core.dto.event.EventInsertDto;
import br.com.metaro.portal.core.entities.Event;
import br.com.metaro.portal.core.repositories.EventRepository;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.util.picture.Picture;
import br.com.metaro.portal.util.picture.PictureService;
import br.com.metaro.portal.util.picture.dto.PictureMinDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.io.IOException;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PictureService pictureService;

    @Cacheable("event")
    @Transactional(readOnly = true)
    public EventDto getEvent() {
        return eventRepository.findNextEvent(Instant.now()).map(EventDto::new).orElse(null);
    }

    @Cacheable("events")
    @Transactional(readOnly = true)
    public List<EventDto> getEvents() {
        return eventRepository.findUpcomingEvents(Instant.now()).stream().map(EventDto::new).toList();
    }

    @Cacheable("eventCount")
    @Transactional
    public Long getEventsCount() {
        return eventRepository.countByEventDateAfter(Instant.now());
    }

    @CacheEvict(cacheNames = {"event", "events", "eventCount", "homeInfo"}, allEntries = true)
    @Transactional
    public EventDto insert(EventInsertDto dto) throws IOException {
        validate(dto, true);
        Picture picture = pictureService.savePostImages(List.of(dto.getImage()), null).getFirst();
        Event event = new Event();
        apply(event, dto);
        event.setPicture(picture);
        Event saved = eventRepository.save(event);
        return new EventDto(saved.getId(), saved.getTitle(), saved.getEventDate(), saved.getUpdatedAt(), new PictureMinDto(picture));
    }

    @CacheEvict(cacheNames = {"event", "events", "eventCount", "homeInfo"}, allEntries = true)
    @Transactional
    public EventDto update(Long id, EventInsertDto dto) throws IOException {
        Event event = eventRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        validate(dto, false);
        apply(event, dto);
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            pictureService.replace(event.getPicture().getId(), dto.getImage());
        }
        Event saved = eventRepository.save(event);
        return new EventDto(saved.getId(), saved.getTitle(), saved.getEventDate(), saved.getUpdatedAt(), new PictureMinDto(saved.getPicture()));
    }

    @CacheEvict(cacheNames = {"event", "events", "eventCount", "homeInfo"}, allEntries = true)
    @Transactional
    public void delete(Long id) throws IOException {
        Long pictureId = eventRepository.findPictureIdById(id)
                .orElseThrow(ResourceNotFoundException::new);
        eventRepository.deleteDirectlyById(id);
        pictureService.delete(pictureId);
    }

    private void validate(EventInsertDto dto, boolean imageRequired) {
        if (dto.getTitle() == null || dto.getTitle().isBlank())
            throw new UnprocessableEntityException("O título do evento é obrigatório.");
        if (dto.getEventDate() == null)
            throw new UnprocessableEntityException("A data do evento é obrigatória.");
        if (imageRequired && (dto.getImage() == null || dto.getImage().isEmpty()))
            throw new UnprocessableEntityException("A imagem do evento é obrigatória.");
    }

    private void apply(Event event, EventInsertDto dto) {
        Instant now = Instant.now();
        event.setTitle(dto.getTitle().trim());
        event.setEventDate(dto.getEventDate());
        if (event.getCreatedAt() == null) event.setCreatedAt(now);
        event.setUpdatedAt(now);
    }
}
