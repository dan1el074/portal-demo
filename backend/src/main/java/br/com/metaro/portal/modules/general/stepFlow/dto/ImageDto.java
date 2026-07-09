package br.com.metaro.portal.modules.general.stepFlow.dto;

import br.com.metaro.portal.util.picture.Picture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ImageDto {
    private Long id;
    private String name;
    private String size;
    private Instant createdAt;
    private Boolean isCurrentStep;

    public ImageDto(Picture picture, Boolean isCurrentStep) {
        this.id = picture.getId();
        this.name = picture.getName();
        this.size = picture.getSize();
        this.createdAt = picture.getCreatedAt();
        this.isCurrentStep = isCurrentStep;
    }
}
