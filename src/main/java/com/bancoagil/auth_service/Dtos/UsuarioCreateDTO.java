package com.bancoagil.auth_service.Dtos;

import com.bancoagil.auth_service.Enums.TipoUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

/**
 * DTO para la creación de un nuevo Usuario.
 * Contiene solo los campos necesarios para el registro y anotaciones de validación.
 */
@Value // Genera Getters, constructor AllArgsConstructor y hace la clase inmutable
@Builder // Patrón Builder
public class UsuarioCreateDTO {

    // El email es el nombre de usuario y debe ser válido.
    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "El formato del email no es válido.")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres.")
    String email;

    // La contraseña es obligatoria. Se validará la fortaleza en el Service.
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    @Size(max = 255, message = "La contraseña no puede exceder los 255 caracteres.")
    String password;

    // El tipo de usuario (CLIENTE, ASESOR, etc.) es obligatorio para el registro.
    @NotNull(message = "El tipo de usuario es obligatorio.")
    TipoUsuario tipoUsuario;
    
    // El campo 'activo' se omite porque debe ser manejado por la lógica de negocio 
    // (p.ej., true por defecto o false hasta que se valide el email).
    // Los campos de fecha (creación/actualización) se generan en la Entity/Service.
}