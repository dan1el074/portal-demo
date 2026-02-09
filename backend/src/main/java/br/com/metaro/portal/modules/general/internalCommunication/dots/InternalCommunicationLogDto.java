package br.com.metaro.portal.modules.general.internalCommunication.dots;

import br.com.metaro.portal.core.dto.UserSummaryDto;
import br.com.metaro.portal.modules.general.internalCommunication.entities.InternalCommunicationLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InternalCommunicationLogDto {
    private Long id;
    private UserSummaryDto user;
    private String content;
    private Instant createdAt;

    public InternalCommunicationLogDto(InternalCommunicationLog entity) {
        id = entity.getId();
        content = entity.getContent();
        createdAt = entity.getCreatedAt();

        if (entity.getUser() != null) {
            user = new UserSummaryDto(entity.getUser());
        }
    }
}
