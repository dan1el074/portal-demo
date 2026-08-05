package br.com.metaro.portal.util.video;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tb_video")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private VideoProvider provider;

    @Column(nullable = false, unique = true)
    private String providerVideoId;

    @Column(nullable = false)
    private String playbackUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private VideoStatus status;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @PrePersist
    public void initializeDefaults() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = VideoStatus.PENDING;
        }
    }
}
