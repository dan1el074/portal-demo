package br.com.metaro.portal.modules.engineering.trelloIntegration.repository;

import br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationRecord;
import br.com.metaro.portal.modules.engineering.trelloIntegration.repository.projections.TrelloIntegrationRecordProjection;
import br.com.metaro.portal.modules.engineering.trelloIntegration.repository.projections.TrelloIntegrationSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface TrelloIntegrationRepository extends JpaRepository<TrelloIntegrationRecord, Long> {
    boolean existsByOrderNumberAndProductCode(Long orderNumber, String productCode);

    @Query("""
        SELECT r.id AS id, r.orderNumber AS orderNumber, r.orderType AS orderType,
               r.client AS client, r.productCode AS productCode,
               r.productDescription AS productDescription, r.quantity AS quantity,
               r.seller AS seller, r.releaseAt AS releaseAt,
               r.expectedDelivery AS expectedDelivery, r.status AS status,
               r.importedAt AS importedAt, r.destinationEmail AS destinationEmail,
               r.sentAt AS sentAt, r.lastResentAt AS lastResentAt,
               r.errorMessage AS errorMessage
        FROM TrelloIntegrationRecord r
        WHERE :search = ''
           OR CAST(r.orderNumber AS string) LIKE CONCAT('%', :search, '%')
           OR LOWER(r.client) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(r.productCode) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(r.productDescription) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(CASE
               WHEN r.status = br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus.SENT THEN 'enviado'
               WHEN r.status = br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus.PENDING THEN 'pendente'
               WHEN r.status = br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus.ERROR THEN 'erro no envio'
               ELSE '' END) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<TrelloIntegrationRecordProjection> search(Pageable pageable, @Param("search") String search);

    @Query("""
        SELECT r.id AS id, r.orderNumber AS orderNumber, r.orderType AS orderType,
               r.client AS client, r.productCode AS productCode,
               r.productDescription AS productDescription, r.quantity AS quantity,
               r.seller AS seller, r.releaseAt AS releaseAt,
               r.expectedDelivery AS expectedDelivery, r.status AS status,
               r.importedAt AS importedAt, r.destinationEmail AS destinationEmail,
               r.sentAt AS sentAt, r.lastResentAt AS lastResentAt,
               r.errorMessage AS errorMessage
        FROM TrelloIntegrationRecord r
        WHERE r.id = :id
    """)
    Optional<TrelloIntegrationRecordProjection> findProjectedById(@Param("id") Long id);

    @Query("""
        SELECT COUNT(r.id) AS total,
               COALESCE(SUM(CASE WHEN r.status = br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus.SENT THEN 1 ELSE 0 END), 0) AS sent,
               COALESCE(SUM(CASE WHEN r.status = br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus.PENDING THEN 1 ELSE 0 END), 0) AS pending,
               COALESCE(SUM(CASE WHEN r.status = br.com.metaro.portal.modules.engineering.trelloIntegration.entity.TrelloIntegrationStatus.ERROR THEN 1 ELSE 0 END), 0) AS errors
        FROM TrelloIntegrationRecord r
    """)
    TrelloIntegrationSummaryProjection summarize();

    @Modifying
    @Transactional
    @Query("DELETE FROM TrelloIntegrationRecord r WHERE r.importedAt < :threshold")
    int deleteImportedBefore(@Param("threshold") Instant threshold);
}
