package br.com.metaro.portal.modules.general.memorando.repository;

import br.com.metaro.portal.modules.general.memorando.entities.MemorandoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemorandoLogRepository extends JpaRepository<MemorandoLog, Long> {
}
