package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.event.EventDto;
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
    @Transactional(readOnly = true)
    public EventDto getEvent() {
        return eventRepository.findNextEvent(Instant.now()).map(EventDto::new).orElse(null);
    }

    @Cacheable("eventCount")
    @Transactional
    public Long getEventsCount() {
        return eventRepository.countByEventDateAfter(Instant.now());
    }
}
