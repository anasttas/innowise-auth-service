package com.kharlamova.auth_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDto {
    private Long id;

    @NotBlank(message = "Email should not be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    private String email;

    @NotBlank(message = "Name should not be blank")
    @Size(min = 1, max = 50, message = "Name should be between {min} and {max} characters")
    private String name;

    @NotBlank(message = "Surname should not be blank")
    @Size(min = 1, max = 50, message = "Surname should be between {min} and {max} characters")
    private String surname;

    @Past(message = "Birth date should be in the past")
    @NotNull(message = "Birth date should not be null")
    private LocalDate birthDate;

    @NotBlank(message = "Login should not be blank")
    private String login;

    @Size(min = 8, max = 20, message = "Password should be more than {min} and less than {max} symbols")
    @NotBlank(message = "Password should not be blank")
    private String password;
}
