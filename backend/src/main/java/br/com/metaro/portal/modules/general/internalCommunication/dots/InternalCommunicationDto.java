package br.com.metaro.portal.modules.general.internalCommunication.dots;

import br.com.metaro.portal.core.dto.PositionDto;
import br.com.metaro.portal.core.dto.UserSummaryDto;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.modules.general.internalCommunication.InternalCommunication;
import br.com.metaro.portal.modules.general.internalCommunication.InternalCommunicationLog;
import br.com.metaro.portal.modules.general.internalCommunication.InternalCommunicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InternalCommunicationDto {
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
    private InternalCommunicationStatus status;
    private List<InternalCommunicationLogDto> logs;

    public InternalCommunicationDto(InternalCommunication entity) {
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
        user = new UserSummaryDto(entity.getCreatedBy());
        fromDepartments = new ArrayList<>();
        interactions = new ArrayList<>();
        logs = new ArrayList<>();

        for (Position department : entity.getFromDepartments()) {
            fromDepartments.add(new PositionDto(department));
        }

        for (User currentInteraction : entity.getInteractions()) {
            interactions.add(new UserSummaryDto(currentInteraction));
        }

        for (InternalCommunicationLog log : entity.getLogs()) {
            logs.add(new InternalCommunicationLogDto(log));
        }
    }
}
