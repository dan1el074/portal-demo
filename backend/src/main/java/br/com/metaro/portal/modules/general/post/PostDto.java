package br.com.metaro.portal.modules.general.post;

import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.util.picture.Picture;
import br.com.metaro.portal.util.picture.dto.PictureDto;
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

    private List<PictureDto> pictures = new ArrayList<>();

    public PostDto(Post post) {
        id = post.getId();
        content = post.getContent();
        createdAt = post.getCreatedAt();
        updateAt = post.getUpdateAt();
        author = new UserSummaryDto(post.getAuthor());

        for (Picture picture : post.getPictures()) {
            this.pictures.add(new PictureDto(picture));
        }
    }
}
