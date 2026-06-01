package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.event.EventDto;
import br.com.metaro.portal.core.entities.Event;
import br.com.metaro.portal.core.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Cacheable("events")
    @Transactional
    public EventDto getEvent() {
        Event event = eventRepository.findFirstByEventDateAfterOrderByEventDateAsc(Instant.now()).orElse(null);

        EventDto eventDto = null;
        if (event != null) eventDto = new EventDto(event);

        return eventDto;
    }

    @Cacheable("eventCount")
    @Transactional
    public Long getEventsCount() {
        return eventRepository.countByEventDateAfter(Instant.now());
    }
}
