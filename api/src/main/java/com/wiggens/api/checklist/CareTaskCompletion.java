package com.wiggens.api.checklist;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "care_task_completions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareTaskCompletion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private CareTask task;

    @Column(nullable = false)
    private Instant completedAt;

    private String completedBy;
}

