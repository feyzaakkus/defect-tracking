package com.feyza.defect_tracking.repository;

import com.feyza.defect_tracking.entity.Defect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefectRepository extends JpaRepository<Defect, Long> {

}
