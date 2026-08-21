package br.com.metaro.portal.util.email;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tb_email_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 255)
    private String subject;
    @Column(nullable = false, length = 320)
    private String recipient;
    @Column(nullable = false, length = 100)
    private String module;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmailStatus status;
    @Column(length = 2000)
    private String errorMessage;
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;
}
