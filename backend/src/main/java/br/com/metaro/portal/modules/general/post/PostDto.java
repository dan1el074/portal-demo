package br.com.metaro.portal.modules.general.post;

import br.com.metaro.portal.core.dto.UserSummaryDto;
import br.com.metaro.portal.util.File;
import br.com.metaro.portal.util.FileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostDto {
    private Long id;
    private String content;
    private UserSummaryDto author;
    private Instant createdAt;
    private Instant updateAt;

    private List<FileDto> pictures = new ArrayList<>();

    public PostDto(Post post) {
        id = post.getId();
        content = post.getContent();
        createdAt = post.getCreatedAt();
        updateAt = post.getUpdateAt();
        author = new UserSummaryDto(post.getAuthor());

        for (File file : post.getFiles()) {
            this.pictures.add(new FileDto(file));
        }
    }
}
