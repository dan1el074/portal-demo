package br.com.metaro.portal.modules.general.rawMaterials.dto;

import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialUserAccessDto {
    private Long id;
    private String name;
    private Long pictureId;
    private List<Long> categoryIds;
}
