package com.feyza.defect_tracking.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private Long defectId;
    private String commentText;
    private String createdByUsername;
    private LocalDateTime createdDate;
}

