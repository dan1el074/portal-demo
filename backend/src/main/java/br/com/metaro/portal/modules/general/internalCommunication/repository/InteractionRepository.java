package br.com.metaro.portal.modules.general.internalCommunication.repository;

import br.com.metaro.portal.modules.general.internalCommunication.entities.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
}
