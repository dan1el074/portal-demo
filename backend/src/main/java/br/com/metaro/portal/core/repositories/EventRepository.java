package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    public Optional<Event> findFirstByEventDateAfterOrderByEventDateAsc(Instant now);
    public long countByEventDateAfter(Instant now);
}
