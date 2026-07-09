package br.com.metaro.portal.modules.general.post.repositories;

import br.com.metaro.portal.modules.general.post.entities.Post;
import br.com.metaro.portal.modules.general.post.repositories.projections.PostProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
        SELECT p.id
        FROM Post p
        ORDER BY p.id DESC
        LIMIT 4
    """)
    public List<Long> findTop4Ids();

    @Query("""
        SELECT
            p.id AS id,
            p.content AS content,
            p.isWarning AS isWarning,
            p.createdAt AS createdAt,
            u.id AS authorId,
            u.name AS authorName,
            pic.id AS authorPictureId,
            pos.name AS authorPositionName,
            pp.id AS pictureId
        FROM Post p
        JOIN p.author u
        LEFT JOIN u.picture pic
        LEFT JOIN u.position pos
        LEFT JOIN p.pictures pp
        WHERE p.id IN :ids
        ORDER BY p.id DESC, pp.id ASC
    """)
    public List<PostProjection> findFeedByIds(@Param("ids") List<Long> ids);

    @Query("SELECT p FROM Post p WHERE p.id < :lastId ORDER BY p.id DESC")
    public List<Post> findBeforeId(@Param("lastId") Long lastId, Pageable pageable);
}
