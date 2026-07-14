package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.repositories.projections.RoleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("""
        SELECT r.id
        FROM User u
        JOIN u.roles r
        WHERE u.id = :id
    """)
    public List<Long> findRoleIds(Long id);

    @Query("""
        SELECT
            r.id AS id,
            r.authority AS authority,
            r.title AS title,
            r.titleUrl AS titleUrl,
            r.parent AS parent,
            r.parentUrl AS parentUrl
        FROM User u
        JOIN u.roles r
        WHERE u.username = :username
    """)
    public List<RoleProjection> findRoleProjectionsByUsername(String username);

    @Query("""
        SELECT
            r.id AS id,
            r.authority AS authority,
            r.title AS title,
            r.titleUrl AS titleUrl,
            r.parent AS parent,
            r.parentUrl AS parentUrl
        FROM Role r
        WHERE r.authority <> 'ROLE_USER'
    """)
    public List<RoleProjection> findAllAdminRoles();
}
