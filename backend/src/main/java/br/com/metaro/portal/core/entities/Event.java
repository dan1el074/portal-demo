package br.com.metaro.portal.core.entities;

import br.com.metaro.portal.util.picture.Picture;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tb_event")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String title;
    private Instant eventDate;
    private Instant createdAt;
    private Instant updatedAt;

    @OneToOne
    @MapsId
    private Picture picture;
}
