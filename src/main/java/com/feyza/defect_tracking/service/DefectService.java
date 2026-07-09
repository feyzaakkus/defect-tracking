package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.dto.DefectCreateRequest;
import com.feyza.defect_tracking.dto.DefectResponse;
import com.feyza.defect_tracking.dto.DefectUpdateRequest;
import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.entity.User;
import com.feyza.defect_tracking.enums.Status;
import com.feyza.defect_tracking.exception.BusinessException;
import com.feyza.defect_tracking.exception.ResourceNotFoundException;
import com.feyza.defect_tracking.repository.DefectRepository;
import com.feyza.defect_tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DefectService {

    private final DefectRepository defectRepository;
    private final UserRepository userRepository;

    public DefectResponse createDefect(DefectCreateRequest request) {
        User currentTester = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Mock Tester user not found."));

        Defect defect = new Defect();
        defect.setTitle(request.getTitle());
        defect.setDescription(request.getDescription());
        defect.setSeverity(request.getSeverity());
        defect.setPriority(request.getPriority());
        defect.setStatus(Status.OPEN);
        defect.setCreatedBy(currentTester);
        defect.setCreatedDate(LocalDateTime.now());
        defect.setUpdatedDate(LocalDateTime.now());

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
                .orElseThrow(() -> new ResourceNotFoundException("Defect not found with id: " + id));
        return convertToResponse(defect);
    }

    public DefectResponse updateDefect(Long id, DefectUpdateRequest request) {
        Defect defect = defectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Defect not found with id: " + id));

        if (defect.getStatus() != request.getStatus()) {
            validateStatusTransition(defect.getStatus(), request.getStatus());
        }

        defect.setTitle(request.getTitle());
        defect.setDescription(request.getDescription());
        defect.setSeverity(request.getSeverity());
        defect.setPriority(request.getPriority());
        defect.setStatus(request.getStatus());
        defect.setUpdatedDate(LocalDateTime.now());

        Defect updatedDefect = defectRepository.save(defect);
        return convertToResponse(updatedDefect);
    }

    public void deleteDefect(Long id) {
        Defect defect = defectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Defect not found with id: " + id));

        defectRepository.delete(defect);
    }

    @Transactional(readOnly = true)
    public Page<DefectResponse> getDefectsByStatus(Status status, Pageable pageable) {
        return defectRepository.findByStatus(status, pageable)
                .map(this::convertToResponse);
    }

    public DefectResponse updateDefectStatus(Long id, Status newStatus) {
        Defect defect = defectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Defect not found with id: " + id));

        validateStatusTransition(defect.getStatus(), newStatus);

        defect.setStatus(newStatus);
        defect.setUpdatedDate(LocalDateTime.now());

        Defect updatedDefect = defectRepository.save(defect);
        return convertToResponse(updatedDefect);
    }

    private void validateStatusTransition(Status oldStatus, Status newStatus) {
        if (oldStatus == newStatus) {
            return;
        }

        switch (oldStatus) {
            case OPEN:
                if (newStatus != Status.ASSIGNED) {
                    throw new BusinessException("Invalid transition! From OPEN, you can only move to ASSIGNED.");
                }
                break;
            case ASSIGNED:
                if (newStatus != Status.FIXED) {
                    throw new BusinessException("Invalid transition! From ASSIGNED, you can only move to FIXED.");
                }
                break;
            case FIXED:
                if (newStatus != Status.VERIFIED) {
                    throw new BusinessException("Invalid transition! From FIXED, you can only move to VERIFIED.");
                }
                break;
            case VERIFIED:
                if (newStatus != Status.CLOSED) {
                    throw new BusinessException("Invalid transition! From VERIFIED, you can only move to CLOSED.");
                }
                break;
            case CLOSED:
                throw new BusinessException("Invalid transition! CLOSED defects cannot be changed.");
            default:
                throw new BusinessException("Unknown status transition.");
        }
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

