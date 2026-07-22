package com.feyza.defect_tracking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefectUpdateRequest {

    @NotBlank(message = "Description cannot be empty.")
    private String description;

}