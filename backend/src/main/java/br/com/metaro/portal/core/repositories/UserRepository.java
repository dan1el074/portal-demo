package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.dto.user.UserMinDto;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.projections.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
        SELECT new br.com.metaro.portal.core.dto.user.UserMinDto(
            u.id,
            p.id,
            u.name,
            u.username,
            pos.name,
            u.email,
            u.activated,
            u.updateAt
        )
        FROM User u
        LEFT JOIN u.picture p
        LEFT JOIN u.position pos
        ORDER BY u.name
    """)
    public List<UserMinDto> findAllMin();

    @Query("""
        SELECT
            pos.name AS position,
            u.id AS id,
            u.name AS name
        FROM User u
        JOIN u.position pos
        WHERE u.activated = true
        ORDER BY pos.name, u.name
    """)
    public List<UserGroupProjection> findAllGrouped();

    @Query("""
        SELECT
            u.id AS id,
            p.id AS pictureId,
            u.name AS name,
            pos.id AS positionId,
            u.email AS email,
            u.birthDate AS birthDate,
            u.username AS username,
            u.activated AS activated,
            u.supportToken AS supportToken
        FROM User u
        LEFT JOIN u.picture p
        LEFT JOIN u.position pos
        WHERE u.id = :id
    """)
    public Optional<UserEditProjection> findEditById(Long id);

    @Query("""
        SELECT
            u.id AS id,
            u.name AS name,
            u.email AS email,
            pos.name AS position,
            u.birthDate AS birthDate,
            pic.id AS pictureId,
            u.activated AS activated,
            u.username AS username,
            u.supportToken AS supportToken
        FROM User u
        LEFT JOIN u.position pos
        LEFT JOIN u.picture pic
        WHERE u.username = :username
    """)
    public Optional<MeProjection> findMeProjectionByUsername(String username);

    @Query("""
        SELECT
            n.type AS type,
            n.referenceId AS referenceId
        FROM User u
        JOIN u.notifications n
        WHERE u.username = :username
    """)
    public List<NotificationMinProjection> findNotificationsByUsername(String username);

    @Query("""
        SELECT
            p.id AS pictureId,
            pos.name AS position,
            u.name AS name,
            u.email AS email,
            u.birthDate AS birthDate
        FROM User u
        LEFT JOIN u.picture p
        LEFT JOIN u.position pos
        WHERE u.username = :username
    """)
    public Optional<UserConfigProjection> findConfigByUsername(@Param("username") String username);

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
        SELECT
            r.id AS id,
            r.authority AS authority
        FROM User u
        JOIN u.roles r
        WHERE u.username = :username
    """)
    public List<RoleProjection> findRoleProjectionsByUsername(String username);

    @Query("""
        SELECT u FROM User u
        JOIN FETCH u.position
        JOIN FETCH u.roles
        LEFT JOIN FETCH u.picture
        WHERE u.username = :username
    """)
    public Optional<User> findMeByUsername(@Param("username") String username);
}
