package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.projections.BirthdayProjection;
import br.com.metaro.portal.core.repositories.projections.UserAuthProjection;
import br.com.metaro.portal.core.repositories.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(nativeQuery = true, value = """
        SELECT tb_user.username AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority 
        FROM tb_user 
        INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id 
        INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id 
        WHERE tb_user.username = :username
    """)
    public List<UserDetailsProjection> searchUserAndRolesByUsername(String username);

    @Query("""
        SELECT u.id AS id, u.activated AS activated
        FROM User u
        WHERE u.username = :username
    """)
    public Optional<UserAuthProjection> findAuthDataByUsername(@Param("username") String username);

    @Query("""
        SELECT
            u.id AS id,
            u.name AS name,
            pic.id AS pictureId,
            u.birthDate AS birthDate,
            pos.name AS positionName
        FROM User u
        LEFT JOIN u.picture pic
        LEFT JOIN u.position pos
        WHERE MONTH(u.birthDate) = MONTH(CURRENT_DATE)
          AND u.activated = true
    """)
    public List<BirthdayProjection> findBirthdaysOfCurrentMonth();

    @Query("""
        SELECT u FROM User u
        JOIN FETCH u.position
        JOIN FETCH u.roles
        LEFT JOIN FETCH u.picture
        WHERE u.username = :username
    """)
    public Optional<User> findMeByUsername(@Param("username") String username);
}
