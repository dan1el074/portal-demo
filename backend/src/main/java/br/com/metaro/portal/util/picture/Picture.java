package br.com.metaro.portal.util.picture;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.modules.general.post.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_picture")
@Data
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String path;
    @Enumerated(EnumType.STRING)
    private PictureType type;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne(mappedBy = "picture")
    private User user;
}
