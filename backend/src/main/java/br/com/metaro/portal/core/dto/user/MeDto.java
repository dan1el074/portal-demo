package br.com.metaro.portal.core.dto.user;

import br.com.metaro.portal.core.dto.notification.NotificationDto;
import br.com.metaro.portal.core.dto.role.RoleDto;
import br.com.metaro.portal.core.entities.Notification;
import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.entities.User;
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
    private final List<NotificationDto> notifications = new ArrayList<>();

    public MeDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.position = user.getPosition().getName();
        this.birthDate = user.getBirthDate();
        this.activated = user.getActivated();
        this.username = user.getUsername();

        if (user.getSupportToken() != null) this.supportToken = user.getSupportToken();
        if (user.getPicture() != null) this.pictureId = user.getPicture().getId();

        for(Role role : user.getRoles()) {
            roles.add(new RoleDto(role));
        }

        for (Notification notification : user.getNotifications()) {
            notifications.add(new NotificationDto(notification));
        }
    }
}
