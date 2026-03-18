package br.com.metaro.portal.modules.general.memorando.dots;

import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemorandoLogDto {
    private Long id;
    private UserSummaryDto user;
    private String content;
    private Instant createdAt;

    public MemorandoLogDto(MemorandoLog entity) {
        id = entity.getId();
        content = entity.getContent();
        createdAt = entity.getCreatedAt();

        if (entity.getUser() != null) {
            user = new UserSummaryDto(entity.getUser());
        }
    }
}
