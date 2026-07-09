package com.feyza.defect_tracking.repository;

import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefectRepository extends JpaRepository<Defect, Long> {
    Page<Defect> findByStatus(Status status, Pageable pageable);
}

