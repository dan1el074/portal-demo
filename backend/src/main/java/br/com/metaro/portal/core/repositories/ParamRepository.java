package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.entities.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParamRepository extends JpaRepository<Param, Long> {
}
