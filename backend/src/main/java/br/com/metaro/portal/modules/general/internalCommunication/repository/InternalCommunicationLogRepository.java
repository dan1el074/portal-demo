package br.com.metaro.portal.modules.general.internalCommunication.repository;

import br.com.metaro.portal.modules.general.internalCommunication.entities.InternalCommunicationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalCommunicationLogRepository extends JpaRepository<InternalCommunicationLog, Long> {
}
