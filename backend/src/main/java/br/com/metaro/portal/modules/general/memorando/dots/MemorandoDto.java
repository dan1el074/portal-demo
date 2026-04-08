package br.com.metaro.portal.modules.general.memorando.dots;

import br.com.metaro.portal.core.dto.position.PositionDto;
import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.modules.general.memorando.entities.Signature;
import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoLog;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<SignatureDto> signatures;
    private MemorandoStatus status;
    private List<MemorandoLogDto> logs;

    // TODO: extrair isso para um outro DTO, usado somente para listar os itens na tabela do frontend
    private Set<UserSummaryDto> signatureSummary;

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
        signatures = new ArrayList<>();
        signatureSummary = new HashSet<>();
        logs = new ArrayList<>();

        for (Position department : entity.getFromDepartments()) {
            fromDepartments.addLast(new PositionDto(department));
        }

        for (Signature sign : entity.getSignatures()) {
            signatures.addLast(new SignatureDto(sign));
            if (
                sign.getIsSign() &&
                signatureSummary.stream().noneMatch(s -> s.getName().equals(sign.getUser().getName()))
            ) {
                signatureSummary.add(new UserSummaryDto(sign.getUser()));
            }
        }

        for (MemorandoLog log : entity.getLogs()) {
            logs.addLast(new MemorandoLogDto(log));
        }
    }
}
