package com.feyza.defect_tracking.controller;

import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.service.DefectService;
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
    public Defect createDefect(@RequestBody Defect defect) {
        return defectService.createDefect(defect);
    }

    @GetMapping
    public Page<Defect> getAllDefects(Pageable pageable) {
        return defectService.getAllDefects(pageable);
    }

    @GetMapping("/{id}")
    public Defect getDefectById(@PathVariable Long id) {
        return defectService.getDefectById(id);
    }
}

