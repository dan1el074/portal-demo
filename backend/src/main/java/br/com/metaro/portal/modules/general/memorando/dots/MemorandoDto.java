package br.com.metaro.portal.modules.general.memorando.dots;

import br.com.metaro.portal.core.dto.position.PositionDto;
import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.modules.general.memorando.entities.Interaction;
import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoLog;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemorandoDto {
    private Long id;
    private Long number;
    private Long request;
    private String client;
    private List<String> items;
    private String title;
    private String description;
    private String reason;
    private Instant createAt;
    private UserSummaryDto user;
    private List<PositionDto> fromDepartments;
    private List<InteractionDto> interactions;
    private List<UserSummaryDto> interactionsSummary;
    private MemorandoStatus status;
    private List<MemorandoLogDto> logs;

    public MemorandoDto(Memorando entity) {
        id = entity.getId();
        number = entity.getNumber();
        request = entity.getRequest();
        client = entity.getClient();
        title = entity.getTitle();
        description = entity.getDescription();
        reason = entity.getReason();
        createAt = entity.getCreateAt();
        status = entity.getStatus();
        user = new UserSummaryDto(entity.getCreatedBy());
        items = new ArrayList<>();
        items.addAll(entity.getItems());
        fromDepartments = new ArrayList<>();
        interactions = new ArrayList<>();
        interactionsSummary = new ArrayList<>();
        logs = new ArrayList<>();

        for (Position department : entity.getFromDepartments()) {
            fromDepartments.add(new PositionDto(department));
        }

        for (Interaction currentInteraction : entity.getInteractions()) {
            interactions.add(new InteractionDto(currentInteraction));
            interactionsSummary.add(new UserSummaryDto(currentInteraction.getUser()));
        }

        for (MemorandoLog log : entity.getLogs()) {
            logs.add(new MemorandoLogDto(log));
        }
    }
}
