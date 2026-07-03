package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.dto.DefectCreateRequest;
import com.feyza.defect_tracking.dto.DefectResponse;
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

    public DefectResponse createDefect(DefectCreateRequest request) {

        Defect defect = new Defect();
        defect.setTitle(request.getTitle());
        defect.setDescription(request.getDescription());
        defect.setSeverity(request.getSeverity());
        defect.setPriority(request.getPriority());
        defect.setStatus(Status.OPEN);

        Defect savedDefect = defectRepository.save(defect);

        return convertToResponse(savedDefect);
    }

    @Transactional(readOnly = true)
    public Page<DefectResponse> getAllDefects(Pageable pageable) {

        return defectRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public DefectResponse getDefectById(Long id) {

        Defect defect = defectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Defect not found with id: " + id));

        return convertToResponse(defect);
    }

    private DefectResponse convertToResponse(Defect defect) {

        DefectResponse response = new DefectResponse();

        response.setId(defect.getId());
        response.setTitle(defect.getTitle());
        response.setDescription(defect.getDescription());
        response.setSeverity(defect.getSeverity());
        response.setPriority(defect.getPriority());
        response.setStatus(defect.getStatus());
        response.setCreatedDate(defect.getCreatedDate());
        response.setUpdatedDate(defect.getUpdatedDate());

        return response;
    }
}