package br.com.metaro.portal.modules.general.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = {"author", "pictures"})
    List<Post> findTop4ByOrderByCreatedAtDesc();
}
