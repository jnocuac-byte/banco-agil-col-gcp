package com.bancoagil.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// DTO para actualizar la información de un asesor
@Data
public class ActualizarAsesorDTO {

    // Nombres del asesor
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres") // Validación de tamaño
    private String nombres;

    // Apellidos del asesor
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres") // Validación de tamaño
    private String apellidos;

    // Código del empleado
    @NotBlank(message = "El área es obligatoria")
    private String area; // CREDITO, RIESGO, ADMINISTRACION
}