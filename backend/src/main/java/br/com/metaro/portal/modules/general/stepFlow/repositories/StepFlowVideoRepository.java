package br.com.metaro.portal.modules.general.stepFlow.repositories;

import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStep;
import br.com.metaro.portal.util.video.Video;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StepFlowVideoRepository extends Repository<OrderStep, Long> {
    @Query("""
        SELECT video
        FROM OrderStep step
        JOIN step.videos video
        WHERE video.id = :videoId
    """)
    Optional<Video> findVideoById(@Param("videoId") Long videoId);
}
