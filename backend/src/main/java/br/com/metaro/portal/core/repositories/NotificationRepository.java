package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.entities.Notification;
import br.com.metaro.portal.core.entities.NotificationType;
import br.com.metaro.portal.core.repositories.projections.NotificationProjection;
import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("""
        SELECT
            n.id AS id,
            n.message AS message,
            n.actionUrl AS actionUrl,
            n.viewed AS viewed,
            n.autoDelete AS autoDelete,
            n.type AS type,
            n.referenceId AS referenceId,
            n.createdAt AS createdAt,
            cb.id AS createdById,
            cb.name AS createdByName,
            cbPic.id AS createdByPictureId,
            cbPos.name AS createdByPositionName
        FROM Notification n
        LEFT JOIN n.createdBy cb
        LEFT JOIN cb.picture cbPic
        LEFT JOIN cb.position cbPos
        WHERE n.user.id = :userId
        ORDER BY n.createdAt DESC
    """)
    public List<NotificationProjection> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    public List<Notification> findByReferenceIdAndType(Long referenceId, NotificationType type);

    public long countByUserIdAndViewedFalse(Long userId);

    public Optional<Notification> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.viewed = true
        WHERE n.user.id = :userId
        AND n.viewed = false
    """)
    public void markAllAsViewedByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("""
        DELETE FROM Notification n
        WHERE n.user.id = :userId
        AND n.autoDelete = true
    """)
    public void deleteAllAutoDeleteByUserId(@Param("userId") Long userId);
}
