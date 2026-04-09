package br.com.metaro.portal.modules.general.memorando.dots;

import br.com.metaro.portal.core.dto.user.UserSummaryDto;
import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import br.com.metaro.portal.modules.general.memorando.entities.Signature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemorandoListDto {
    private Long id;
    private Long number;
    private Long request;
    private String client;
    private MemorandoStatus status;
    private Set<UserSummaryDto> signatureSummary;
    private Instant createAt;

    public MemorandoListDto(Memorando entity) {
        id = entity.getId();
        number = entity.getNumber();
        request = entity.getRequest();
        client = entity.getClient();
        status = entity.getStatus();
        signatureSummary = new HashSet<>();
        createAt = entity.getCreateAt();

        for (Signature sign : entity.getSignatures()) {
            if (
                sign.getIsSign() &&
                signatureSummary.stream().noneMatch(s -> s.getName().equals(sign.getUser().getName()))
            ) {
                signatureSummary.add(new UserSummaryDto(sign.getUser()));
            }
        }
    }
}
