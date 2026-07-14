package com.feyza.defect_tracking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateRequest {

    @NotNull(message = "Defect ID is required.")
    private Long defectId;

    @NotBlank(message = "Comment text cannot be empty.")
    private String commentText;
}

