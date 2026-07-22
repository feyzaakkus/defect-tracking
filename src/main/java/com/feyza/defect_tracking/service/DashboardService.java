package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.dto.response.DashboardResponse;
import com.feyza.defect_tracking.dto.response.DefectResponse;
import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.enums.Severity;
import com.feyza.defect_tracking.enums.Status;
import com.feyza.defect_tracking.repository.DefectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final DefectRepository defectRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public DashboardResponse getDashboardData() {
        DashboardResponse response = new DashboardResponse();

        response.setTotalDefects(defectRepository.count());

        long openCount = defectRepository.countByStatus(Status.OPEN) + defectRepository.countByStatus(Status.ASSIGNED);
        long resolvedCount = defectRepository.countByStatus(Status.FIXED) + defectRepository.countByStatus(Status.VERIFIED);
        long closedCount = defectRepository.countByStatus(Status.CLOSED);

        response.setOpenDefects(openCount);
        response.setResolvedDefects(resolvedCount);
        response.setClosedDefects(closedCount);

        Map<Severity, Long> severityMap = new HashMap<>();
        for (Severity severity : Severity.values()) {
            severityMap.put(severity, defectRepository.countBySeverity(severity));
        }
        response.setSeverityCounts(severityMap);

        List<DefectResponse> recentCreated = defectRepository.findTop5ByOrderByCreatedDateDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        List<DefectResponse> recentClosed = defectRepository.findTop5ByStatusOrderByUpdatedDateDesc(Status.CLOSED)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        response.setRecentCreatedDefects(recentCreated);
        response.setRecentClosedDefects(recentClosed);

        return response;
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

