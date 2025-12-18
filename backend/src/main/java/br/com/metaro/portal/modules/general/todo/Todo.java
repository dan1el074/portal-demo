package br.com.metaro.portal.modules.general.todo;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tb_todo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String varchar;
    private Boolean completed;
    private Instant createdAt;
    private Instant updatedAt;
}
