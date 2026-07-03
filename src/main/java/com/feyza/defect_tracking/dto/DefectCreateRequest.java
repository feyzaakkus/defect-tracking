package com.feyza.defect_tracking.dto;

import com.feyza.defect_tracking.enums.Priority;
import com.feyza.defect_tracking.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefectCreateRequest {

    @NotBlank(message = "Title cannot be empty.")
    @Size(max = 100, message = "Title can be up to 100 characters.")
    private String title;

    @NotBlank(message = "Description cannot be empty.")
    private String description;

    @NotNull(message = "Severity is required.")
    private Severity severity;

    @NotNull(message = "Priority is required.")
    private Priority priority;
}
