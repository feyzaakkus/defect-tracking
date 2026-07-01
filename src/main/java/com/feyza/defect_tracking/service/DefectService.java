package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.enums.Status;
import com.feyza.defect_tracking.repository.DefectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefectService {

    private final DefectRepository defectRepository;

    public Defect createDefect(Defect defect) {
        defect.setStatus(Status.OPEN);
        return defectRepository.save(defect);
    }

    public Page<Defect> getAllDefects(Pageable pageable) {
        return defectRepository.findAll(pageable);
    }

    public Defect getDefectById(Long id) {
        return defectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Defect bulunamadı."));
    }
}