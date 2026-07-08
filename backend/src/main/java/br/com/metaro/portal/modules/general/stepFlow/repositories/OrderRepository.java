package br.com.metaro.portal.modules.general.stepFlow.repositories;

import br.com.metaro.portal.modules.general.stepFlow.entities.Order;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStatus;
import br.com.metaro.portal.modules.general.stepFlow.entities.StepStatus;
import br.com.metaro.portal.modules.general.stepFlow.entities.StepType;
import br.com.metaro.portal.modules.general.stepFlow.repositories.projections.StatusCountsProjection;
import br.com.metaro.portal.modules.general.stepFlow.repositories.projections.StepCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
        SELECT
            COUNT(o) AS totalCount,
            COALESCE(SUM(CASE WHEN o.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), 0) AS progressCount,
            COALESCE(SUM(CASE WHEN o.status = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completeCount,
            COALESCE(SUM(CASE WHEN o.status = 'LATE' THEN 1 ELSE 0 END), 0) AS lateCount
        FROM Order o
    """)
    public Optional<StatusCountsProjection> findStatusCounts();

    @Query("""
        SELECT o.currentStep AS step, COUNT(o) AS count
        FROM Order o
        WHERE o.status <> :status
        GROUP BY o.currentStep
    """)
    public List<StepCountProjection> findCountByStep(@Param("status") OrderStatus status);

    @Query("""
        SELECT o
        FROM Order o
        JOIN o.steps s
        WHERE o.currentStep = :step
            AND s.step = :step
            AND s.status = :status
            AND o.status <> :orderStatus
    """)
    List<Order> findByCurrentStep(@Param("step") StepType step, @Param("status") StepStatus status, @Param("orderStatus") OrderStatus orderStatus);

    @Query("""
        SELECT o
        FROM Order o
        WHERE o.number = :orderNumber
            AND o.status <> :orderStatus
    """)
    List<Order> findByNumber(Integer orderNumber, @Param("orderStatus") OrderStatus orderStatus);

    @Query("""
        SELECT o
        FROM Order o
        WHERE
            LOWER(o.client) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(o.currentStep) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(o.status) LIKE LOWER(CONCAT('%', :search, '%'))
            OR CAST(o.number AS string) LIKE CONCAT('%', :search, '%')
    """)
    Page<Order> search(@Param("search") String search, Pageable pageable);
}
