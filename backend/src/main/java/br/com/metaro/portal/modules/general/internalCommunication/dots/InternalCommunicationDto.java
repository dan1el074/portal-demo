package br.com.metaro.portal.modules.general.internalCommunication.dots;

import br.com.metaro.portal.core.dto.PositionDto;
import br.com.metaro.portal.core.dto.UserSummaryDto;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.modules.general.internalCommunication.internalCommunication;
import br.com.metaro.portal.modules.general.internalCommunication.internalCommunicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InternalControlDto {
    private Long id;
    private Long number;
    private Long request;
    private String client;
    private String item;
    private String title;
    private String description;
    private String reason;
    private Instant createAt;
    private UserSummaryDto user;
    private List<PositionDto> fromDepartments;
    private List<UserSummaryDto> interactions;
    private internalCommunicationStatus status;

    public InternalControlDto(internalCommunication entity) {
        id = entity.getId();
        number = entity.getNumber();
        request = entity.getRequest();
        client = entity.getClient();
        item = entity.getItem();
        title = entity.getTitle();
        description = entity.getDescription();
        reason = entity.getReason();
        createAt = entity.getCreateAt();
        status = entity.getStatus();
        user = new UserSummaryDto(entity.getUser());
        fromDepartments = new ArrayList<>();
        interactions = new ArrayList<>();

        for (Position department : entity.getFromDepartments()) {
            fromDepartments.add(new PositionDto(department));
        }

        for (User currentInteraction : entity.getInteractions()) {
            interactions.add(new UserSummaryDto(currentInteraction));
        }
    }
}
