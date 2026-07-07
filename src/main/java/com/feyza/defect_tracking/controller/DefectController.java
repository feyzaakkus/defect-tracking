package com.feyza.defect_tracking.controller;

import com.feyza.defect_tracking.dto.DefectCreateRequest;
import com.feyza.defect_tracking.dto.DefectResponse;
import com.feyza.defect_tracking.dto.DefectUpdateRequest;
import com.feyza.defect_tracking.enums.Status;
import com.feyza.defect_tracking.service.DefectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public Page<DefectResponse> getAllDefects(Pageable pageable) {
        return defectService.getAllDefects(pageable);
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
    public Page<DefectResponse> getDefectsByStatus(@PathVariable Status status, Pageable pageable) {
        return defectService.getDefectsByStatus(status, pageable);
    }

    @PatchMapping("/{id}/status")
    public DefectResponse updateDefectStatus(@PathVariable Long id, @RequestParam Status status) {
        return defectService.updateDefectStatus(id, status);
    }
}
