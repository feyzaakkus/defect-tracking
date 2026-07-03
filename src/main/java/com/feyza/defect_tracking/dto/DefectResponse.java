package com.feyza.defect_tracking.dto;

import com.feyza.defect_tracking.enums.Priority;
import com.feyza.defect_tracking.enums.Severity;
import com.feyza.defect_tracking.enums.Status;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class DefectResponse {
    private Long id;
    private String title;
    private String description;
    private Severity severity;
    private Priority priority;
    private Status status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}