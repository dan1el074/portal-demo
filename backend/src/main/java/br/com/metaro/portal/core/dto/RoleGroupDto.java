package br.com.metaro.portal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleGroupDto {
    private String title;
    private List<RoleSummaryDto> childrens;
}
