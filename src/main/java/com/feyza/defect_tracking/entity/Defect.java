package com.feyza.defect_tracking.entity;

import com.feyza.defect_tracking.enums.Priority;
import com.feyza.defect_tracking.enums.Severity;
import com.feyza.defect_tracking.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "defects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Defect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_developer")
    private User assignedDeveloper;

    @Column(columnDefinition = "TEXT")
    private String resolutionNote;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
