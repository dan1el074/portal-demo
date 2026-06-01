package br.com.metaro.portal.core.dto.event;

import br.com.metaro.portal.core.entities.Event;
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

    public EventDto (Event event) {
        id = event.getId();
        title = event.getTitle();
        eventDate = event.getEventDate();

        if (event.getPicture() != null) {
            picture = new PictureMinDto(event.getPicture());
        }
    }
}
