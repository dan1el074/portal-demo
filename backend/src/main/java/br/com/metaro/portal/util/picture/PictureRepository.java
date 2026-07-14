package br.com.metaro.portal.util.picture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    @Query("""
        SELECT
            p.id AS id,
            p.path AS path
        FROM Picture p
        WHERE p.id = :id
    """)
    public Optional<PictureDeleteProjection> findDeleteProjectionById(Long id);

    @Modifying
    @Query("""
        DELETE FROM Picture p
        WHERE p.id = :id
    """)
    public void deleteDirectlyById(@Param("id") Long id);

    @Query("""
        select case when count(u) > 0 then true else false end
        from User u
        where u.picture.id = :pictureId
          and (:excludingUserId is null or u.id <> :excludingUserId)
    """)
    boolean existsUserReference(@Param("pictureId") Long pictureId, @Param("excludingUserId") Long excludingUserId);

    @Query("""
        select case when count(e) > 0 then true else false end
        from Event e
        where e.picture.id = :pictureId
    """)
    boolean existsEventReference(@Param("pictureId") Long pictureId);
}
