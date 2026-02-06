package br.com.metaro.portal.modules.general.internalCommunication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalCommunicationRepository extends JpaRepository<InternalCommunication, Long> {
}
