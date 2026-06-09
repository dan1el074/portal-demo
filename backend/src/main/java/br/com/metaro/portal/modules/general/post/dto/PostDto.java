package br.com.metaro.portal.modules.general.post.dto;

import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.modules.general.post.entities.Post;
import br.com.metaro.portal.util.picture.Picture;
import br.com.metaro.portal.util.picture.dto.PictureMinDto;
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
    private Boolean isWarning;
    private UserSummaryDto author;
    private Instant createdAt;

    private List<PictureMinDto> pictures = new ArrayList<>();

    public PostDto(Post post) {
        id = post.getId();
        content = post.getContent();
        isWarning = post.getIsWarning();
        author = new UserSummaryDto(post.getAuthor());
        createdAt = post.getCreatedAt();

        for (Picture picture : post.getPictures()) {
            this.pictures.add(new PictureMinDto(picture));
        }
    }
}
