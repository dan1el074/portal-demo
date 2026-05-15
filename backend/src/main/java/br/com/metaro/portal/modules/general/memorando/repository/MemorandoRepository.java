package br.com.metaro.portal.modules.general.memorando.repository;

import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemorandoRepository extends JpaRepository<Memorando, Long> {
    @Query("""
        SELECT DISTINCT m
        FROM Memorando m
        LEFT JOIN FETCH m.fromDepartments d
        WHERE m.id = :id
    """)
    public Optional<Memorando> findByIdWithDepartments(Long id);
}
