package br.com.metaro.portal.modules.general.internalControl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalControlRepository extends JpaRepository<InternalControl, Long> {
}
