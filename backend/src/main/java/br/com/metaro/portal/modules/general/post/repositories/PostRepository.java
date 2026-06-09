package br.com.metaro.portal.modules.general.post.repositories;

import br.com.metaro.portal.modules.general.post.entities.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = {"author", "pictures"})
    List<Post> findTop4ByOrderByIdDesc();

    @Query("SELECT p FROM Post p WHERE p.id < :lastId ORDER BY p.id DESC")
    List<Post> findBeforeId(@Param("lastId") Long lastId, Pageable pageable);
}
