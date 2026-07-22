package com.feyza.defect_tracking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username") // <-- SQL çakışmasını önleyen kritik eklenti!
    private String user;

    private String action;

    private String entityType;

    private Long entityId;

    private LocalDateTime actionDate;
}
