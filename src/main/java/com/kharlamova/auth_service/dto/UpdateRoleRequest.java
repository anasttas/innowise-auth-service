package com.kharlamova.auth_service.dto;

import com.kharlamova.auth_service.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRoleRequest {
    @NotBlank(message = "Login should not be blank")
    private String login;

    @NotNull(message = "Role should not be blank")
    private Role role;
}
