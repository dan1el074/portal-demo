package br.com.metaro.portal.core.dto.user;

import br.com.metaro.portal.core.dto.notification.PendingIssuesDto;
import br.com.metaro.portal.core.dto.role.RoleDto;
import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.projections.MeProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MeDto {

    private Long id;
    private String name;
    private String email;
    private String position;
    private LocalDate birthDate;
    private Long pictureId;
    private Boolean activated;
    private String username;
    private String supportToken;

    private final List<RoleDto> roles = new ArrayList<>();
    private final List<PendingIssuesDto> pendingIssues = new ArrayList<>();

    public MeDto(MeProjection projection) {
        this.id = projection.getId();
        this.name = projection.getName();
        this.email = projection.getEmail();
        this.position = projection.getPosition();
        this.birthDate = projection.getBirthDate();
        this.pictureId = projection.getPictureId();
        this.activated = projection.getActivated();
        this.username = projection.getUsername();
        this.supportToken = projection.getSupportToken();
    }
}
