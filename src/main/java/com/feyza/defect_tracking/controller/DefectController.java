package com.feyza.defect_tracking.controller;

import com.feyza.defect_tracking.dto.request.DefectCreateRequest;
import com.feyza.defect_tracking.dto.request.DefectFilterRequest;
import com.feyza.defect_tracking.dto.request.DefectUpdateRequest;
import com.feyza.defect_tracking.dto.response.DefectResponse;
import com.feyza.defect_tracking.enums.Priority;
import com.feyza.defect_tracking.enums.Severity;
import com.feyza.defect_tracking.enums.Status;
import com.feyza.defect_tracking.service.DefectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/defects")
@RequiredArgsConstructor
public class DefectController {

    private final DefectService defectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DefectResponse createDefect(@Valid @RequestBody DefectCreateRequest request) {
        return defectService.createDefect(request);
    }

    @GetMapping
    public Page<DefectResponse> getAllDefects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return defectService.getAllDefects(pageable);
    }

    @GetMapping("/search")
    public Page<DefectResponse> filterDefects(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long assignedDeveloperId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        DefectFilterRequest filterRequest = new DefectFilterRequest();
        filterRequest.setStatus(status);
        filterRequest.setSeverity(severity);
        filterRequest.setPriority(priority);
        filterRequest.setAssignedDeveloperId(assignedDeveloperId);

        Pageable pageable = PageRequest.of(page, size);
        return defectService.filterDefects(filterRequest, pageable);
    }

    @GetMapping("/{id}")
    public DefectResponse getDefectById(@PathVariable Long id) {
        return defectService.getDefectById(id);
    }

    @PutMapping("/{id}")
    public DefectResponse updateDefect(@PathVariable Long id, @Valid @RequestBody DefectUpdateRequest request) {
        return defectService.updateDefect(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDefect(@PathVariable Long id) {
        defectService.deleteDefect(id);
    }

    @GetMapping("/status/{status}")
    public Page<DefectResponse> getDefectsByStatus(
            @PathVariable Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return defectService.getDefectsByStatus(status, pageable);
    }

    @PatchMapping("/{id}/assign")
    public DefectResponse assignDefect(@PathVariable Long id, @RequestParam Long developerId) {
        return defectService.assignDefect(id, developerId);
    }

    @PatchMapping("/{id}/status")
    public DefectResponse updateDefectStatus(
            @PathVariable Long id,
            @RequestParam Status status,
            @RequestParam(required = false) String resolutionNote) {
        return defectService.updateDefectStatus(id, status, resolutionNote);
    }
}
