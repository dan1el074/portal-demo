package br.com.metaro.portal.modules.general.post;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.util.File;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_post")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updateAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @OneToMany(mappedBy = "post")
    private List<File> files = new ArrayList<>();
}
