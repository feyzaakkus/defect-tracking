package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.dto.request.DefectCreateRequest;
import com.feyza.defect_tracking.dto.response.DefectResponse;
import com.feyza.defect_tracking.dto.request.DefectUpdateRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DefectService {

    private final DefectRepository defectRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('TESTER')")
    public DefectResponse createDefect(DefectCreateRequest request) {
        User currentTester = getCurrentUser();

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

    @PreAuthorize("hasRole('TESTER')")
    public DefectResponse updateDefect(Long id, DefectUpdateRequest request) {
        Defect defect = defectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Defect not found with id: " + id));

        User currentUser = getCurrentUser();

        if (!defect.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new BusinessException("Only the TESTER who created this defect can update its details!");
        }


        defect.setTitle(request.getTitle());
        defect.setDescription(request.getDescription());
        defect.setSeverity(request.getSeverity());
        defect.setPriority(request.getPriority());
        defect.setUpdatedDate(LocalDateTime.now());

        Defect updatedDefect = defectRepository.save(defect);
        return convertToResponse(updatedDefect);
    }

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasAnyRole('TESTER', 'ADMIN')")
    public DefectResponse assignDefect(Long id, Long developerId) {
        Defect defect = defectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Defect not found with id: " + id));

        User developer = userRepository.findById(developerId)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with id: " + developerId));

        if (!"DEVELOPER".equals(developer.getRole().name())) {
            throw new BusinessException("You can only assign a defect to a DEVELOPER!");
        }

        validateStatusTransition(defect.getStatus(), Status.ASSIGNED);

        defect.setAssignedDeveloper(developer);
        defect.setStatus(Status.ASSIGNED);
        defect.setUpdatedDate(LocalDateTime.now());

        Defect updatedDefect = defectRepository.save(defect);
        return convertToResponse(updatedDefect);
    }

    @PreAuthorize("hasAnyRole('TESTER', 'DEVELOPER')")
    public DefectResponse updateDefectStatus(Long id, Status newStatus, String resolutionNote) {
        Defect defect = defectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Defect not found with id: " + id));

        User currentUser = getCurrentUser();
        validateStatusTransition(defect.getStatus(), newStatus);

        if (newStatus == Status.FIXED) {
            if (defect.getAssignedDeveloper() == null || !defect.getAssignedDeveloper().getId().equals(currentUser.getId())) {
                throw new BusinessException("Only the assigned DEVELOPER can mark this defect as FIXED!");
            }
            if (resolutionNote == null || resolutionNote.trim().isEmpty()) {
                throw new BusinessException("Resolution note is required when resolving a defect!");
            }
            defect.setResolutionNote(resolutionNote);
        }

        if (newStatus == Status.VERIFIED) {
            if (!defect.getCreatedBy().getId().equals(currentUser.getId())) {
                throw new BusinessException("Only the TESTER who created this defect can verify it!");
            }
        }

        if (newStatus == Status.CLOSED) {
            if (!defect.getCreatedBy().getId().equals(currentUser.getId())) {
                throw new BusinessException("Only the TESTER who created this defect can close it!");
            }
        }

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

    private User getCurrentUser() {
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
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
        if (defect.getAssignedDeveloper() != null) {
            response.setAssignedDeveloperId(defect.getAssignedDeveloper().getId());
        }
        response.setResolutionNote(defect.getResolutionNote());
        return response;
    }
}

