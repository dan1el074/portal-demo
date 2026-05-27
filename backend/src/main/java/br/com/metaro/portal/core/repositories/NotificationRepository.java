package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.entities.Notification;
import br.com.metaro.portal.core.entities.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByReferenceIdAndType(Long referenceId, NotificationType type);

    long countByUserIdAndViewedFalse(Long userId);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.viewed = true
        WHERE n.user.id = :userId
        AND n.viewed = false
    """)
    void markAllAsViewedByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("""
        DELETE FROM Notification n
        WHERE n.user.id = :userId
        AND n.autoDelete = true
    """)
    void deleteAllAutoDeleteByUserId(@Param("userId") Long userId);
}
