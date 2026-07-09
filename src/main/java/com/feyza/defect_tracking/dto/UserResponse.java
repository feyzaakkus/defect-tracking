package com.feyza.defect_tracking.dto;

import com.feyza.defect_tracking.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String username;
    private Role role;
}

