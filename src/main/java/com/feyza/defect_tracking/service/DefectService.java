package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.enums.Status;
import com.feyza.defect_tracking.repository.DefectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DefectService {

    private final DefectRepository defectRepository;

    public Defect createDefect(Defect defect) {
        defect.setStatus(Status.OPEN);
        return defectRepository.save(defect);
    }

    @Transactional(readOnly = true)
    public Page<Defect> getAllDefects(Pageable pageable) {
        return defectRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Defect getDefectById(Long id) {
        return defectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Defect not found with id: " + id));
    }
}