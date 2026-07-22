package com.feyza.defect_tracking.repository;

import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.enums.Severity;
import com.feyza.defect_tracking.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DefectRepository extends JpaRepository<Defect, Long>, JpaSpecificationExecutor<Defect> {

    Page<Defect> findByStatus(Status status, Pageable pageable);

    long countByStatus(Status status);

    long countBySeverity(Severity severity);

    List<Defect> findTop5ByOrderByCreatedDateDesc();

    List<Defect> findTop5ByStatusOrderByUpdatedDateDesc(Status status);
}
