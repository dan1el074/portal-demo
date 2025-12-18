package br.com.metaro.portal.util;
import br.com.metaro.portal.modules.general.post.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_picture")
@Data
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String path;
    @Enumerated(EnumType.STRING)
    private FileType type;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
