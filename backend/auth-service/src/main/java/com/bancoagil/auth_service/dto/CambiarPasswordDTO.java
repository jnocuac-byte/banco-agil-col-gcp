package com.bancoagil.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// DTO para cambiar la contraseña de un usuario
@Data
public class CambiarPasswordDTO {

    // Contraseña actual del usuario
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String passwordActual;

    // Nueva contraseña del usuario
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres") // Validación de tamaño
    private String passwordNueva;
}