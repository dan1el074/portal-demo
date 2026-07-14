package br.com.metaro.portal.modules.general.memorando.repository.projections;

import java.time.Instant;

public interface MemorandoPendingProjection {
    Long getId();
    Long getNumber();
    Instant getCreateAt();
}
