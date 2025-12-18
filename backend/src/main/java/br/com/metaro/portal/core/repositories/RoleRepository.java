package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
