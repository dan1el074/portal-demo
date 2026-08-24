package br.com.metaro.portal.modules.general.rawMaterials.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialAccessUpdateDto {
    @NotNull
    @Valid
    private List<UserCategoriesDto> users;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCategoriesDto {
        @NotNull private Long id;
        @NotNull private List<Long> categoryIds;
    }
}
