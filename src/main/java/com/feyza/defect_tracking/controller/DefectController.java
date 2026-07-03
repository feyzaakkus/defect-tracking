package com.feyza.defect_tracking.controller;

import com.feyza.defect_tracking.dto.DefectCreateRequest;
import com.feyza.defect_tracking.dto.DefectResponse;
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
}