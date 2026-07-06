package br.com.metaro.portal.modules.general.post.repositories.projections;

import java.time.Instant;

public interface PostProjection {
    Long getId();
    String getContent();
    Boolean getIsWarning();
    Instant getCreatedAt();
    Long getAuthorId();
    String getAuthorName();
    Long getAuthorPictureId();
    String getAuthorPositionName();
    Long getPictureId();
}
