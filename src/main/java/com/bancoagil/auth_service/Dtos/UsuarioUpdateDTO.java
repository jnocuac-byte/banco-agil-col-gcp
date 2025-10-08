package com.bancoagil.auth_service.Dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;


/**
 * DTO para la actualización parcial de un Usuario.
 * Los campos son opcionales (no llevan @NotBlank).
 */
@Value
@Builder
public class UsuarioUpdateDTO {

    // El email puede ser actualizado, pero debe ser un email válido si se proporciona.
    @Email(message = "El formato del email no es válido.")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres.")
    String email;

    // Se puede actualizar el estado de la cuenta.
    Boolean activo;

}