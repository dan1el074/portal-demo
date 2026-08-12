package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.entities.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParamRepository extends JpaRepository<Param, Long> {
    Optional<Param> findByName(String name);
}
