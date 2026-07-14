package br.com.metaro.portal.util.picture;

import br.com.metaro.portal.core.entities.Event;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.modules.general.post.entities.Post;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStep;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tb_picture")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String path;
    @Enumerated(EnumType.STRING)
    private PictureType type;
    private String size;
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private OrderStep orderStep;

    @OneToOne(mappedBy = "picture", fetch = FetchType.LAZY)
    private User user;

    @OneToOne(mappedBy = "picture", fetch = FetchType.LAZY)
    private Event event;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }
}
