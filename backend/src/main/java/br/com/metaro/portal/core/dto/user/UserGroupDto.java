package br.com.metaro.portal.core.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserGroupDto {
    String title;
    List<UserSummaryMinDto> childrens;
}
