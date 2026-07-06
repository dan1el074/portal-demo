package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.entities.Event;
import br.com.metaro.portal.core.repositories.projections.EventProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("""
        SELECT
            e.id AS id,
            e.title AS title,
            e.eventDate AS eventDate,
            e.picture.id AS pictureId
        FROM Event e
        WHERE e.eventDate > :now
        ORDER BY e.eventDate ASC
        LIMIT 1
    """)
    public Optional<EventProjection> findNextEvent(@Param("now") Instant now);

    public Long countByEventDateAfter(Instant date);
}
