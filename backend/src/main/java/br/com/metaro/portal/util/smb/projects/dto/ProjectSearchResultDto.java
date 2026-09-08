package br.com.metaro.portal.util.smb.projects.dto;

import br.com.metaro.portal.util.smb.projects.ProjectSource;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectSearchResultDto {
    private String fileName;
    private ProjectSource source;
}
