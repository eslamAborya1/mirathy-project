package com.NTG.mirathy.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String description;

    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private InheritanceProblem problem;
}
