package com.bancoagil.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO para manejar la información de inicio de sesión
@Data
public class LoginDTO {

    // Email del usuario
    @NotBlank(message="El email es obligatorio")
    @Email(message="Email Inválido") // Validación de formato de email
    private String email;

    // Contraseña del usuario
    @NotBlank(message="La contraseña es obligatoria")
    private String password;

}