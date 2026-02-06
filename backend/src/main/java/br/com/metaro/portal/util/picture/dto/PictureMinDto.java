package br.com.metaro.portal.util.picture.dto;

import br.com.metaro.portal.util.picture.Picture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PictureMinDto {
    private Long id;

    public PictureMinDto(Picture picture) {
        id = picture.getId();
    }
}
