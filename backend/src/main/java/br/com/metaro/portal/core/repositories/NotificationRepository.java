package br.com.metaro.portal.core.repositories;

import br.com.metaro.portal.core.entities.Notification;
import br.com.metaro.portal.core.entities.NotificationType;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByReferenceIdAndType(Long referenceId, NotificationType type);

    long countByUserIdAndViewedFalse(Long userId);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);
}
