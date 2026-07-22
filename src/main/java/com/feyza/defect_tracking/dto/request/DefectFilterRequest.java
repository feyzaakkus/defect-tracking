package com.feyza.defect_tracking.dto.request;

import com.feyza.defect_tracking.enums.Priority;
import com.feyza.defect_tracking.enums.Severity;
import com.feyza.defect_tracking.enums.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefectFilterRequest {
    private Status status;
    private Severity severity;
    private Priority priority;
    private Long assignedDeveloperId;
}
