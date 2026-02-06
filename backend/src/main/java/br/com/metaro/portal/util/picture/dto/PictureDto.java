package br.com.metaro.portal.util.picture.dto;

import br.com.metaro.portal.util.picture.Picture;
import br.com.metaro.portal.util.picture.PictureType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PictureDto {
    private Long id;
    private String name;
    private String path;
    private PictureType type;
    private Long post_id;

    public PictureDto(Picture picture) {
        id = picture.getId();
        name = picture.getName();
        path = picture.getPath();
        type = picture.getType();

        if (picture.getPost() != null) post_id = picture.getPost().getId();
    }
}
