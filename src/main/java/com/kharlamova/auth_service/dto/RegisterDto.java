package com.kharlamova.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDto {
    private Long id;

    @NotNull(message = "User id should not be blank")
    private Long userId;

    @NotBlank(message = "Login should not be blank")
    private String login;

    @Size(min = 8, max = 20, message = "Password should be more than {min} and less than {max} symbols")
    @NotBlank(message = "Password should not be blank")
    private String password;
}
