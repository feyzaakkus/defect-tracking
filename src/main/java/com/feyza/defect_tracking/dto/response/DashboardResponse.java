package com.feyza.defect_tracking.dto.response;

import com.feyza.defect_tracking.enums.Severity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DashboardResponse {

    private long totalDefects;
    private long openDefects;
    private long resolvedDefects;
    private long closedDefects;

    private Map<Severity, Long> severityCounts;

    private List<DefectResponse> recentCreatedDefects;
    private List<DefectResponse> recentClosedDefects;
}
