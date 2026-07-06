package br.com.metaro.portal.core.dto.event;

import br.com.metaro.portal.core.repositories.projections.EventProjection;
import br.com.metaro.portal.util.picture.dto.PictureMinDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventDto {
    private Long id;
    private String title;
    private Instant eventDate;
    private PictureMinDto picture;

    public EventDto(EventProjection projection) {
        this.id = projection.getId();
        this.title = projection.getTitle();
        this.eventDate = projection.getEventDate();
        this.picture = projection.getPictureId() != null ? new PictureMinDto(projection.getPictureId()) : null;
    }
}
